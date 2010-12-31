/*
* Copyright (C) 2002-2003 the xine project
*
* This file is part of xine, a free video player.
*
* xine is free software; you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation; either version 2 of the License, or
* (at your option) any later version.
*
* xine is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA	02111-1307, USA
*
* $Id: mmsh.c,v 1.16 2007/12/11 20:50:43 jwrdegoede Exp $
*
* MMS over HTTP protocol
*	written by Thibaut Mattern
*	based on mms.c and specs from avifile
*	(http://avifile.sourceforge.net/asf-1.0.htm)
*
* TODO:
*	error messages
*	http support cleanup, find a way to share code with input_http.c (http.h|c)
*	http proxy support
*/

#ifdef HAVE_CONFIG_H
#include "config.h"
#endif

#include <unistd.h>
#include <stdio.h>
#include <stdlib.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>
#include <string.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <errno.h>
#include <stdlib.h>
#include <time.h>
#include <assert.h>

#define LOG_MODULE "mmsh"
#define LOG_VERBOSE
#ifdef DEBUG
# define lprintf printf
#else
# define lprintf(x,...)
#endif

/* cheat a bit and call ourselves mms.c to keep the code in mmsio.h clean */
#define __MMS_C__

#include "bswap.h"
#include "mmsh.h"
#include "asfheader.h"
#include "uri.h"

/* #define USERAGENT "User-Agent: NSPlayer/7.1.0.3055\r\n" */
#define USERAGENT "User-Agent: NSPlayer/4.1.0.3856\r\n"
#define CLIENTGUID "Pragma: xClientGUID={c77e7400-738a-11d2-9add-0020af0a3278}\r\n"


#define MMSH_PORT									80
#define MMSH_UNKNOWN								0
#define MMSH_SEEKABLE								1
#define MMSH_LIVE									2

#define CHUNK_HEADER_LENGTH			4
#define EXT_HEADER_LENGTH			8
#define CHUNK_TYPE_RESET			0x4324
#define CHUNK_TYPE_DATA				0x4424
#define CHUNK_TYPE_END				0x4524
#define CHUNK_TYPE_ASF_HEADER		0x4824
#define CHUNK_SIZE					65536	/* max chunk size */
#define ASF_HEADER_SIZE				(8192 * 2)	/* max header size */

#define SCRATCH_SIZE				1024

static const char* mmsh_FirstRequest =
	"GET %s HTTP/1.0\r\n"
	"Accept: */*\r\n"
	USERAGENT
	"Host: %s:%d\r\n"
	"Pragma: no-cache,rate=1.000000,stream-time=0,stream-offset=0:0,request-context=%u,max-duration=0\r\n"
	CLIENTGUID
	"Connection: Close\r\n\r\n";

static const char* mmsh_SeekableRequest =
	"GET %s HTTP/1.0\r\n"
	"Accept: */*\r\n"
	USERAGENT
	"Host: %s:%d\r\n"
	"Pragma: no-cache,rate=1.000000,stream-time=%u,stream-offset=%u:%u,request-context=%u,max-duration=%u\r\n"
	CLIENTGUID
	"Pragma: xPlayStrm=1\r\n"
	"Pragma: stream-switch-count=%d\r\n"
	"Pragma: stream-switch-entry=%s\r\n" /*	ffff:1:0 ffff:2:0 */
	"Connection: Close\r\n\r\n";

static const char* mmsh_LiveRequest =
	"GET %s HTTP/1.0\r\n"
	"Accept: */*\r\n"
	USERAGENT
	"Host: %s:%d\r\n"
	"Pragma: no-cache,rate=1.000000,request-context=%u\r\n"
	"Pragma: xPlayStrm=1\r\n"
	CLIENTGUID
	"Pragma: stream-switch-count=%d\r\n"
	"Pragma: stream-switch-entry=%s\r\n"
	"Connection: Close\r\n\r\n";


#if 0
/* Unused requests */
static const char* mmsh_PostRequest =
	"POST %s HTTP/1.0\r\n"
	"Accept: */*\r\n"
	USERAGENT
	"Host: %s\r\n"
	"Pragma: client-id=%u\r\n"
	/*		"Pragma: log-line=no-cache,rate=1.000000,stream-time=%u,stream-offset=%u:%u,request-context=2,max-duration=%u\r\n" */
	"Pragma: Content-Length: 0\r\n"
	CLIENTGUID
	"\r\n";

static const char* mmsh_RangeRequest =
	"GET %s HTTP/1.0\r\n"
	"Accept: */*\r\n"
	USERAGENT
	"Host: %s:%d\r\n"
	"Range: bytes=%Lu-\r\n"
	CLIENTGUID
	"Connection: Close\r\n\r\n";
#endif



/* 
* mmsh specific types 
*/


struct mmsh_s {

	/* FIXME: de-xine-ification */
	void *custom_data;

	int					s;

	/* url parsing */
	char				*url;
	char				*proxy_url;
	char				*proto;
	char				*connect_host;
	int					connect_port;
	char				*http_host;
	int					http_port;
	int					http_request_number;
	char				*proxy_user;
	char				*proxy_password;
	char				*host_user;
	char				*host_password;
	char				*uri;

	char					str[SCRATCH_SIZE]; /* scratch buffer to built strings */

	int					stream_type;	/* seekable or broadcast */

	/* receive buffer */

	/* chunk */
	uint16_t			chunk_type;
	uint16_t			chunk_length;
	uint32_t			chunk_seq_number;
	uint8_t			buf[CHUNK_SIZE];

	int					buf_size;
	int					buf_read;

	uint8_t			asf_header[ASF_HEADER_SIZE];
	uint32_t			asf_header_len;
	uint32_t			asf_header_read;
	int					num_stream_ids;
	int					stream_ids[ASF_MAX_NUM_STREAMS];
	int					stream_types[ASF_MAX_NUM_STREAMS];
	uint32_t			packet_length;
	int64_t			file_length;
	uint64_t			time_len; /* playback time in 100 nanosecs (10^-7) */
	uint64_t			preroll;
	uint64_t			asf_num_packets;
	char					guid[37];
	uint32_t			bitrates[ASF_MAX_NUM_STREAMS];
	uint32_t			bitrates_pos[ASF_MAX_NUM_STREAMS];

	int					has_audio;
	int					has_video;
	int					seekable;

	mms_off_t				current_pos;
	int					user_bandwidth;
};

int g_bStopMmsh = 0;

static int fallback_io_select(void *data, int socket, int state, int timeout_msec)
{
	int res;
	fd_set set;
	struct timeval tv = { timeout_msec / 1000, (timeout_msec % 1000) * 1000};
	FD_ZERO(&set);
	FD_SET(socket, &set);
	res= select(socket+1, (state == MMS_IO_READ_READY) ? &set : NULL,
		(state == MMS_IO_WRITE_READY) ? &set : NULL, NULL, &tv);
	
	
	if (res==0) res=MMS_IO_STATUS_TIMEOUT;
	else res=MMS_IO_STATUS_READY;
	
	return res;
	
}

static int fallback_io_read(void *data, int socket, char *buf, int num)
{
	time_t seconds = time(NULL);
	off_t len = 0, ret, retry = 0;
	errno = 0;
	while (len < num && !g_bStopMmsh)
	{
		ret = (off_t)read(socket, buf + len, num - len);
		if(ret == 0)
			break; /* EOF */
		if(ret < 0)
		{
			switch(errno)
			{
			case EAGAIN:
				if (retry++ >= 1000 * 15)
				{
//					gMMSError = 1;
					return -1;
				}
				usleep(1000);
				break;
			default:;
				// if already read something, return it, we will fail next time
				return len ? len : ret; 
			}
		} else
			len += ret;

		if ((time(NULL) - seconds ) > 5)
		{
			return len;
		}
		if (g_bStopMmsh == 1)
			return len;
	}
	return len;
}

static int fallback_io_write(void *data, int socket, char *buf, int num)
{
	return (off_t)write(socket, buf, num);
}

static char g_szCurrentHost[64] = {0};

static int fallback_io_tcp_connect(void *data, const char *host, int port)
{
#ifdef WIN32
	unsigned long u = 1;
#endif
	struct hostent *h;
	int i, s;

	h = gethostbyname(host);
	if (h == NULL) {
		// unable to resolve host
		return -1;
	}

	s = socket(PF_INET, SOCK_STREAM, IPPROTO_TCP);	
	if (s == -1) {
		// failed to create socket
		return -1;
	}
	//int val = 1;
	//setsockopt(s, SOL_SOCKET, SO_NOSIGPIPE, &val, sizeof(val));
#ifndef WIN32
//	if (fcntl (s, F_SETFL, fcntl (s, F_GETFL) & ~O_NONBLOCK) == -1) {
	if (fcntl (s, F_SETFL, fcntl (s, F_GETFL) | O_NONBLOCK) == -1) {
		// can't put socket in non-blocking mode
		return -1;
	}
#else
	ioctlsocket(s, FIOASYNC, &u);
#endif
	for (i = 0; h->h_addr_list[i]; i++) {
		struct in_addr ia;
		struct sockaddr_in sin;

		memcpy (&ia, h->h_addr_list[i], 4);
		sin.sin_family = AF_INET;
		sin.sin_addr	= ia;
		sin.sin_port	= htons(port);

		snprintf(g_szCurrentHost, 63, "%i.%i.%i.%i:%i",
			(int) ((ia.s_addr & 0x000000FF) >> 0),
			(int) ((ia.s_addr & 0x0000FF00) >> 8),
			(int) ((ia.s_addr & 0x00FF0000) >> 16),
			(int) ((ia.s_addr & 0xFF000000) >> 24),
			(int) port);

		if (connect(s, (struct sockaddr *)&sin, sizeof(sin)) ==-1 && errno != EINPROGRESS) {
			close(s);
			continue;
		}

		return s;
	}
	return -1;
}


static mms_io_t fallback_io =
{
	&fallback_io_select,
	NULL,
	&fallback_io_read,
	NULL,
	&fallback_io_write,
	NULL,
	&fallback_io_tcp_connect,
	NULL,
};

static mms_io_t default_io =	{
	&fallback_io_select,
	NULL,
	&fallback_io_read,
	NULL,
	&fallback_io_write,
	NULL,
	&fallback_io_tcp_connect,
	NULL,
};

#ifdef WIN32

#define io_read(io, b, c, d) ((io) ? (io)->read(io->read_data , b, c, d) : default_io.read(NULL , b, c, d))
#define io_write(io, b, c, d) ((io) ? (io)->write(io->write_data , b, c, d) : default_io.write(NULL , b, c, d))
#define io_select(io, b, c, d) ((io) ? (io)->select(io->select_data , b, c, d) : default_io.select(NULL , b, c, d))
#define io_connect(io, b, c) ((io) ? (io)->connect(io->connect_data , b, c) : default_io.connect(NULL , b, c))

#else

#define io_read(io, args...) ((io) ? (io)->read(io->read_data , ## args) : default_io.read(NULL , ## args))
#define io_write(io, args...) ((io) ? (io)->write(io->write_data , ## args) : default_io.write(NULL , ## args))
#define io_select(io, args...) ((io) ? (io)->select(io->select_data , ## args) : default_io.select(NULL , ## args))
#define io_connect(io, args...) ((io) ? (io)->connect(io->connect_data , ## args) : default_io.connect(NULL , ## args))

#endif

static int get_guid (unsigned char *buffer, int offset) {
	int i;
	GUID g;

	g.Data1 = LE_32(buffer + offset);
	g.Data2 = LE_16(buffer + offset + 4);
	g.Data3 = LE_16(buffer + offset + 6);
	for(i = 0; i < 8; i++) {
		g.Data4[i] = buffer[offset + 8 + i];
	}

	for (i = 1; i < GUID_END; i++) {
		if (!memcmp(&g, &guids[i].guid, sizeof(GUID))) {
			lprintf ("GUID: %s\n", guids[i].name);

			return i;
		}
	}

	lprintf ("libmmsh: unknown GUID: 0x%x, 0x%x, 0x%x, "
		"{ 0x%hx, 0x%hx, 0x%hx, 0x%hx, 0x%hx, 0x%hx, 0x%hx, 0x%hx }\n",
		g.Data1, g.Data2, g.Data3,
		g.Data4[0], g.Data4[1], g.Data4[2], g.Data4[3], 
		g.Data4[4], g.Data4[5], g.Data4[6], g.Data4[7]);
	return GUID_ERROR;
}

static int send_command (mms_io_t *io, mmsh_t *instance, char *cmd)	{
	int length;

	length = strlen(cmd);
	if (io_write(io, instance->s, cmd, length) != length) {
		// mmsh: send error
		return 0;
	}
	return 1;
}

static int get_answer (mms_io_t *io, mmsh_t *instance) {

	int done, len, linenum;
	char *features;

	done = 0; len = 0; linenum = 0;
	instance->stream_type = MMSH_UNKNOWN;

	while (!done && !g_bStopMmsh) {

		if (io_read(io, instance->s, &(instance->buf[len]), 1) != 1) {
			// mmsh: alert: end of stream
			return 0;
		}

		if (instance->buf[len] == '\012') {

			instance->buf[len] = '\0';
			len--;

			if ((len >= 0) && (instance->buf[len] == '\015')) {
				instance->buf[len] = '\0';
				len--;
			}

			linenum++;

			lprintf ("answer: >%s<\n", instance->buf);

			if (linenum == 1) {
				int httpver, httpsub, httpcode;
				char httpstatus[51] = {0};

				int nsscanf = sscanf(instance->buf, "HTTP/%d.%d %d %50[^\015\012]", &httpver, &httpsub,
					&httpcode, httpstatus);
				if (nsscanf != 3 && nsscanf != 4) {
					// mmsh: bad response format
					return 0;
				}

				if (httpcode >= 300 && httpcode < 400) {
					// mmsh: 3xx redirection not implemented
					return 0;
				}

				if (httpcode < 200 || httpcode >= 300) {
					// mmsh: http status not 2xx
					return 0;
				}
			} else {

				if (!strncasecmp(instance->buf, "Location: ", 10)) {
					// mmsh: Location redirection not implemented
					return 0;
				}

				if (!strncasecmp(instance->buf, "Pragma:", 7)) {
					features = strstr(instance->buf + 7, "features=");
					if (features) {
						if (strstr(features, "seekable")) {
							lprintf("seekable stream\n");
							instance->stream_type = MMSH_SEEKABLE;
							instance->seekable = 1;
						} else {
							if (strstr(features, "broadcast")) {
								lprintf("live stream\n");
								instance->stream_type = MMSH_LIVE;
								instance->seekable = 0;
							}
						}
					}
				}
			}

			if (len == -1) {
				done = 1;
			} else {
				len = 0;
			}
		} else {
			len ++;
		}
	}
	if (instance->stream_type == MMSH_UNKNOWN) {
		lprintf ("mmsh: unknown stream type\n");
		instance->stream_type = MMSH_SEEKABLE; /* FIXME ? */
		instance->seekable = 1;
	}
	return 1;
}

static int get_chunk_header (mms_io_t *io, mmsh_t *instance) {
	uint8_t chunk_header[CHUNK_HEADER_LENGTH];
	uint8_t ext_header[EXT_HEADER_LENGTH];
	int read_len;
	int ext_header_len;

	lprintf ("get_chunk_header\n");

	/* read chunk header */
	read_len = io_read(io, instance->s, chunk_header, CHUNK_HEADER_LENGTH);
	if (read_len != CHUNK_HEADER_LENGTH) {
		lprintf ("chunk header read failed, %d != %d\n", read_len, CHUNK_HEADER_LENGTH);
		return 0;
	}
	instance->chunk_type		= LE_16 (&chunk_header[0]);
	instance->chunk_length		= LE_16 (&chunk_header[2]);

	switch (instance->chunk_type) {
		case CHUNK_TYPE_DATA:
			ext_header_len = 8;
			break;
		case CHUNK_TYPE_END:
			ext_header_len = 4;
			break;
		case CHUNK_TYPE_ASF_HEADER:
			ext_header_len = 8;
			break;
		case CHUNK_TYPE_RESET:
			ext_header_len = 4;
			break;
		default:
			ext_header_len = 0;
	}
	/* read extended header */
	if (ext_header_len > 0) {
		read_len = io_read (io, instance->s, ext_header, ext_header_len);
		if (read_len != ext_header_len) {
			lprintf ("extended header read failed. %d != %d\n", read_len, ext_header_len);
			return 0;
		}
	}

	if (instance->chunk_type == CHUNK_TYPE_DATA || instance->chunk_type == CHUNK_TYPE_END)
		instance->chunk_seq_number = LE_32 (&ext_header[0]);

	/* display debug infos */
#ifdef DEBUG
	switch (instance->chunk_type) {
		case CHUNK_TYPE_DATA:
			lprintf ("chunk type:			CHUNK_TYPE_DATA\n");
			lprintf ("chunk length:			%d\n", instance->chunk_length);
			lprintf ("chunk seq:			%d\n", instance->chunk_seq_number);
			lprintf ("unknown:				%d\n", ext_header[4]);
			lprintf ("mmsh seq:				%d\n", ext_header[5]);
			lprintf ("len2:					%d\n", LE_16(&ext_header[6]));
			break;
		case CHUNK_TYPE_END:
			lprintf ("chunk type:			CHUNK_TYPE_END\n");
			lprintf ("continue:				%d\n", instance->chunk_seq_number);
			break;
		case CHUNK_TYPE_ASF_HEADER:
			lprintf ("chunk type:			CHUNK_TYPE_ASF_HEADER\n");
			lprintf ("chunk length:	%d\n",	instance->chunk_length);
			lprintf ("unknown:				%2X %2X %2X %2X %2X %2X\n",
				ext_header[0], ext_header[1], ext_header[2], ext_header[3],
				ext_header[4], ext_header[5]);
			lprintf ("len2:					%d\n", LE_16(&ext_header[6]));
			break;
		case CHUNK_TYPE_RESET:
			lprintf ("chunk type:			CHUNK_TYPE_RESET\n");
			lprintf ("chunk seq:			%d\n", instance->chunk_seq_number);
			lprintf ("unknown:				%2X %2X %2X %2X\n",
				ext_header[0], ext_header[1], ext_header[2], ext_header[3]);
			break;
		default:
			lprintf ("unknown chunk:		%4X\n", instance->chunk_type);
	}
#endif

	instance->chunk_length -= ext_header_len;
	return 1;
}

static int get_header (mms_io_t *io, mmsh_t *instance) {
	int len = 0;

	lprintf("get_header\n");

	instance->asf_header_len = 0;
	instance->asf_header_read = 0;

	/* read chunk */
	while (!g_bStopMmsh) {
		if (get_chunk_header(io, instance)) {
			if (instance->chunk_type == CHUNK_TYPE_ASF_HEADER) {
				if ((instance->asf_header_len + instance->chunk_length) > ASF_HEADER_SIZE) {
					lprintf ("mmsh: the asf header exceed %d bytes\n", ASF_HEADER_SIZE);
					return 0;
				} else {
					len = io_read(io, instance->s, instance->asf_header + instance->asf_header_len,
						instance->chunk_length);
					if (len == -1)
						return -1;
					instance->asf_header_len += len;
					if (len != instance->chunk_length) {
						return 0;
					}
				}
			} else {
				break;
			}
		} else {
			lprintf("get_chunk_header failed\n");
			return 0;
		}
	}

	if (instance->chunk_type == CHUNK_TYPE_DATA) {
		/* read the first data chunk */
		len = io_read (io, instance->s, instance->buf, instance->chunk_length);

		if (len != instance->chunk_length) {
			return 0;
		} else {
			/* check and 0 pad the first data chunk */
			if (instance->chunk_length > instance->packet_length) {
				lprintf ("mmsh: chunk_length(%d) > packet_length(%d)\n",
					instance->chunk_length, instance->packet_length);
				return 0;
			}

			/* explicit padding with 0 */
			if (instance->chunk_length < instance->packet_length)
				memset(instance->buf + instance->chunk_length, 0,
				instance->packet_length - instance->chunk_length);

			instance->buf_size = instance->packet_length;

			return 1;
		}
	} else {
		/* unexpected packet type */
		return 0;
	}
}

static void interp_header (mms_io_t *io, mmsh_t *instance) {

	int i, pass;

	instance->packet_length = 0;
	instance->num_stream_ids = 0;
	instance->asf_num_packets = 0;
	/*
	* parse asf header
	*/

	for(pass = 0; pass < 2; ++pass) {
		i = 30;
		while ((i + 24) < (int) instance->asf_header_len) {

			int guid;
			uint64_t length;

			guid = get_guid(instance->asf_header, i);
			i += 16;

			length = LE_64(instance->asf_header + i);
			i += 8;

			if ((i + length) >= instance->asf_header_len)
				return;

			switch (guid) {

				case GUID_ASF_FILE_PROPERTIES:
					if(!pass) {
						instance->packet_length = LE_32(instance->asf_header + i + 92 - 24);
						if (instance->packet_length > CHUNK_SIZE) {
							instance->packet_length = 0;
							// asf packet len too large
							break;
						}
						instance->file_length	= LE_64(instance->asf_header + i + 40 - 24);
						instance->time_len		= LE_64(instance->asf_header + i + 64 - 24);
						//instance->time_len	= LE_64(instance->asf_header + i + 72 - 24);
						instance->preroll		= LE_64(instance->asf_header + i + 80 - 24);
					}
					break;

				case GUID_ASF_STREAM_PROPERTIES:
					if(!pass) {
						uint16_t flags;
						uint16_t stream_id;
						int			type;
						int			encrypted;

						guid = get_guid(instance->asf_header, i);
						switch (guid) {
							case GUID_ASF_AUDIO_MEDIA:
								type = ASF_STREAM_TYPE_AUDIO;
								instance->has_audio = 1;
								break;

							case GUID_ASF_VIDEO_MEDIA:
							case GUID_ASF_JFIF_MEDIA:
							case GUID_ASF_DEGRADABLE_JPEG_MEDIA:
								type = ASF_STREAM_TYPE_VIDEO;
								instance->has_video = 1;
								break;

							case GUID_ASF_COMMAND_MEDIA:
								type = ASF_STREAM_TYPE_CONTROL;
								break;

							default:
								type = ASF_STREAM_TYPE_UNKNOWN;
						}

						flags = LE_16(instance->asf_header + i + 48);
						stream_id = flags & 0x7F;
						encrypted = flags >> 15;

						if (instance->num_stream_ids < ASF_MAX_NUM_STREAMS && stream_id < ASF_MAX_NUM_STREAMS) {
							instance->stream_types[stream_id] = type;
							instance->stream_ids[instance->num_stream_ids] = stream_id;
							instance->num_stream_ids++;
						} else {
							// too many streams, skipping
						}


					}
					break;

				case GUID_ASF_STREAM_BITRATE_PROPERTIES:
					if(pass) {
						uint16_t streams = LE_16(instance->asf_header + i);
						uint16_t stream_id;
						int j;

						for(j = 0; j < streams; j++) {
							stream_id = LE_16(instance->asf_header + i + 2 + j * 6);
							if(stream_id < ASF_MAX_NUM_STREAMS) {
								instance->bitrates[stream_id] = LE_32(instance->asf_header + i + 4 + j * 6);
								instance->bitrates_pos[stream_id] = i + 4 + j * 6;
							}
						}
					}
					break;

				case GUID_ASF_DATA:
					if(pass) {
						instance->asf_num_packets = LE_64(instance->asf_header + i + 40 - 24);
					}
					break;

				default:
					// unknown object
					break;
			}
			if (length > 24) {
				i += (int) length - 24;
			}
		}
	}
}

const static char *const mmsh_proto_s[] = { "mms", "mmsh", NULL };

static int mmsh_valid_proto (char *proto) {
	int i = 0;

	lprintf("mmsh_valid_proto\n");

	if (!proto)
		return 0;

	while(mmsh_proto_s[i]) {
		if (!strcasecmp(proto, mmsh_proto_s[i])) {
			return 1;
		}
		i++;
	}
	return 0;
}

/*
* returns 1 on error
*/
static int mmsh_tcp_connect(mms_io_t *io, mmsh_t *instance) {
	int progress, res;

	if (!instance->connect_port)
		instance->connect_port = MMSH_PORT;

	/* 
	* try to connect 
	*/

	instance->s = io_connect (io, instance->connect_host, instance->connect_port);

	if (instance->s == -1) {
		lprintf ("mmsh: failed to connect '%s'\n", instance->connect_host);
		return 1;
	}

	instance->s = io_connect(io, instance->connect_host, instance->connect_port);
	if (instance->s == -1) {
		// failed to connect
		return 1;
	}

	/* connection timeout 15s */
	progress = 0;
	do {
		res = io_select (io, instance->s, MMS_IO_WRITE_READY, 2000);
		progress += 1;
	} while ((res == MMS_IO_STATUS_TIMEOUT) && (progress < 15) &&(!g_bStopMmsh));
	if (res != MMS_IO_STATUS_READY) {
		close (instance->s);
		instance->s = -1;
		return 1;
	}

	return 0;
}

static int mmsh_connect_int (mms_io_t *io, mmsh_t *instance, mms_off_t seek, uint32_t time_seek) {
	int		i;
	int		video_stream = -1;
	int		audio_stream = -1;
	int		max_arate		= -1;
	int		min_vrate		= -1;
	int		min_bw_left	= 0;
	int		stream_id;
	int		bandwitdh_left;
	char	stream_selection[10 * ASF_MAX_NUM_STREAMS]; /* 10 chars per stream */
	int		offset;

	/* Close exisiting connection (if any) and connect */
	if (instance->s != -1)
		close(instance->s);

	if (mmsh_tcp_connect(io, instance)) {
		return 0;
	}

	/*
	* let the negotiations begin...
	*/
	instance->num_stream_ids = 0;

	/* first request */

	memset(instance->str, 0, sizeof(instance->str));
	snprintf (instance->str, SCRATCH_SIZE - 1, mmsh_FirstRequest, instance->uri,
		instance->http_host, instance->http_port, instance->http_request_number++);

	if (!send_command (io, instance, instance->str))
		goto fail;

	if (!get_answer (io, instance))
		goto fail;


	if(get_header(io, instance) == -1)
		goto fail;
	interp_header(io, instance);
	if (!instance->packet_length || !instance->num_stream_ids)
		goto fail;
	if (!instance->packet_length || !instance->num_stream_ids)
		goto fail;

	close(instance->s);


	/* choose the best quality for the audio stream */
	/* i've never seen more than one audio stream */
	for (i = 0; i < instance->num_stream_ids; i++) {
		stream_id = instance->stream_ids[i];
		switch (instance->stream_types[stream_id]) {
			case ASF_STREAM_TYPE_AUDIO:
				if ((audio_stream == -1) || ((int) instance->bitrates[stream_id] > max_arate)) {
					audio_stream = stream_id;
					max_arate = instance->bitrates[stream_id];
				}
				break;
			default:
				break;
		}
	}

	/* choose a video stream adapted to the user bandwidth */
	bandwitdh_left = instance->user_bandwidth - max_arate;
	if (bandwitdh_left < 0) {
		bandwitdh_left = 0;
	}
	lprintf("bandwitdh %d, left %d\n", instance->user_bandwidth, bandwitdh_left);

	min_bw_left = bandwitdh_left;
	for (i = 0; i < instance->num_stream_ids; i++) {
		stream_id = instance->stream_ids[i];
		switch (instance->stream_types[stream_id]) {
			case ASF_STREAM_TYPE_VIDEO:
				if (((bandwitdh_left - (int) instance->bitrates[stream_id]) < min_bw_left) &&
					(bandwitdh_left >= (int) instance->bitrates[stream_id])) {
						video_stream = stream_id;
						min_bw_left = bandwitdh_left - instance->bitrates[stream_id];
				}
				break;
			default:
				break;
		}
	}	

	/* choose the stream with the lower bitrate */
	if ((video_stream == -1) && instance->has_video) {
		for (i = 0; i < instance->num_stream_ids; i++) {
			stream_id = instance->stream_ids[i];
			switch (instance->stream_types[stream_id]) {
				case ASF_STREAM_TYPE_VIDEO:
					if ((video_stream == -1) || 
						((int) instance->bitrates[stream_id] < min_vrate) ||
						(!min_vrate)) {
							video_stream = stream_id;
							min_vrate = instance->bitrates[stream_id];
					}
					break;
				default:
					break;
			}
		}
	}

	lprintf("audio stream %d, video stream %d\n", audio_stream, video_stream);

	/* second request */
	lprintf("second http request\n");

	if (mmsh_tcp_connect(io, instance)) {
		return 0;
	}

	/* stream selection string */
	/* The same selection is done with mmst */
	/* 0 means selected */
	/* 2 means disabled */
	offset = 0;
	for (i = 0; i < instance->num_stream_ids; i++) {
		int size;
		if ((instance->stream_ids[i] == audio_stream) ||
			(instance->stream_ids[i] == video_stream)) {
				size = snprintf(stream_selection + offset, sizeof(stream_selection) - offset,
					"ffff:%d:0 ", instance->stream_ids[i]);
		} else {
			lprintf ("disabling stream %d\n", instance->stream_ids[i]);
			size = snprintf(stream_selection + offset, sizeof(stream_selection) - offset,
				"ffff:%d:2 ", instance->stream_ids[i]);
		}
		if (size < 0) goto fail;
		offset += size;
	}

	switch (instance->stream_type) {
		case MMSH_SEEKABLE:
			snprintf (instance->str, SCRATCH_SIZE, mmsh_SeekableRequest, instance->uri,
				instance->http_host, instance->http_port, time_seek,
				(unsigned int)(seek >> 32),
				(unsigned int)seek, instance->http_request_number++, 0,
				instance->num_stream_ids, stream_selection);
			break;
		case MMSH_LIVE:
			snprintf (instance->str, SCRATCH_SIZE, mmsh_LiveRequest, instance->uri,
				instance->http_host, instance->http_port, instance->http_request_number++,
				instance->num_stream_ids, stream_selection);
			break;
	}

	if (!send_command (io, instance, instance->str))
		goto fail;

	lprintf("before read \n");

	if (!get_answer (io, instance))
		goto fail;

	if (!get_header(io, instance))
		goto fail;

	interp_header(io, instance);
	if (!instance->packet_length || !instance->num_stream_ids)
		goto fail;

	for (i = 0; i < instance->num_stream_ids; i++) {
		if ((instance->stream_ids[i] != audio_stream) &&
			(instance->stream_ids[i] != video_stream)) {
				lprintf("disabling stream %d\n", instance->stream_ids[i]);

				/* forces the asf demuxer to not choose instance stream */
				if (instance->bitrates_pos[instance->stream_ids[i]]) {
					instance->asf_header[instance->bitrates_pos[instance->stream_ids[i]]]		= 0;
					instance->asf_header[instance->bitrates_pos[instance->stream_ids[i]] + 1] = 0;
					instance->asf_header[instance->bitrates_pos[instance->stream_ids[i]] + 2] = 0;
					instance->asf_header[instance->bitrates_pos[instance->stream_ids[i]] + 3] = 0;
				}
		}
	}
	return 1;
fail:
	close(instance->s);
	instance->s = -1;
	return 0;
}

mmsh_t* mmsh_connect (mms_io_t *io, void *data, const char *url, int bandwidth) {
	mmsh_t* instance = NULL;
	GURI* uri = NULL;
	GURI* proxy_uri = NULL;

	g_bStopMmsh = 0;
	if (!url)
		return NULL;

	/*
	* initializatoin is essential here.	the fail: label depends
	* on the various char * in our instance structure to be
	* NULL if they haven't been assigned yet.
	*/
	instance = (mmsh_t*) calloc(1, sizeof(mmsh_t));

	uri = gnet_uri_new(url);
	if (!uri) {
		// invalid url
		goto fail;
	}

	instance->custom_data = data;
	instance->url = strdup(url);
	instance->s = -1;
	instance->user_bandwidth = bandwidth;
	instance->http_request_number = 1;

	if (instance->proxy_url) {
		proxy_uri = gnet_uri_new(instance->proxy_url);
		if (!proxy_uri) {
			// invalid proxy url
			goto fail;
		}
		if (!proxy_uri->port ) {
			proxy_uri->port = 3128; //default squid port
		}
	}

	if (!uri->port ) {
		//checked in tcp_connect, but it's better to initialize it here
		uri->port = MMSH_PORT;
	}
	if (instance->proxy_url) {
		instance->proto = (uri->scheme) ? strdup(uri->scheme) : NULL;
		instance->connect_host = (proxy_uri->hostname) ? strdup(proxy_uri->hostname) : NULL;
		instance->connect_port = proxy_uri->port;
		instance->http_host = (uri->scheme) ? strdup(uri->hostname) : NULL;
		instance->http_port = uri->port;
		instance->proxy_user = (proxy_uri->user) ? strdup(proxy_uri->user) : NULL;
		instance->proxy_password = (proxy_uri->passwd) ? strdup(proxy_uri->passwd) : NULL;
		instance->host_user = (uri->user) ? strdup(uri->user) : NULL;
		instance->host_password = (uri->passwd) ? strdup(uri->passwd) : NULL;
		gnet_uri_set_scheme(uri,"http");
		instance->uri = gnet_mms_helper(uri);
	} else {
		instance->proto = (uri->scheme) ? strdup(uri->scheme) : NULL;
		instance->connect_host = (uri->hostname) ? strdup(uri->hostname) : NULL;
		instance->connect_port = uri->port;
		instance->http_host = (uri->hostname) ? strdup(uri->hostname) : NULL;
		instance->http_port = uri->port;
		instance->proxy_user = NULL;
		instance->proxy_password = NULL;
		instance->host_user =(uri->user) ?	strdup(uri->user) : NULL;
		instance->host_password = (uri->passwd) ? strdup(uri->passwd) : NULL;
		instance->uri = gnet_mms_helper(uri);
	}

	gnet_uri_delete(uri);
	gnet_uri_delete(proxy_uri);

	if(!instance->uri)
		goto fail;

	if (!mmsh_valid_proto(instance->proto)) {
		// unsupported protocol
		goto fail;
	}

	if (!mmsh_connect_int(io, instance, 0, 0))
		goto fail;

	return instance;

fail:
	if (instance->s != -1)
		close(instance->s);
	if (instance->url)
		free(instance->url);
	if (instance->proxy_url)
		free(instance->proxy_url);
	if (instance->proto)
		free(instance->proto);
	if (instance->connect_host)
		free(instance->connect_host);
	if (instance->http_host)
		free(instance->http_host);
	if (instance->proxy_user)
		free(instance->proxy_user);
	if (instance->proxy_password)
		free(instance->proxy_password);
	if (instance->host_user)
		free(instance->host_user);
	if (instance->host_password)
		free(instance->host_password);
	if (instance->uri)
		free(instance->uri);

	free(instance);

	return NULL;
}


/*
* returned value:
*	0: error
*	1: data packet read
*	2: new header and data packet read
*/
static int get_media_packet (mms_io_t *io, mmsh_t *instance) {
	int len = 0;

	lprintf("get_media_packet: instance->packet_length: %d\n", instance->packet_length);

	if (get_chunk_header(io, instance)) {
		switch (instance->chunk_type) {
			case CHUNK_TYPE_END:
				/* instance->chunk_seq_number:
				*		0: stop
				*		1: a new stream follows
				*/
				if (instance->chunk_seq_number == 0)
					return 0;

				instance->http_request_number = 1;
				if (!mmsh_connect_int (io, instance, 0, 0))
					return 0;

				/* What todo with: current_pos ??
				Also our chunk_seq_numbers will probably restart from 0!
				If instance happens with a seekable stream (does it ever?)
				and we get a seek request after instance were fscked! */
				instance->seekable = 0;

				/* mmsh_connect_int reads the first data packet */
				/* instance->buf_size is set by mmsh_connect_int */
				return 2;

			case CHUNK_TYPE_DATA:
				/* nothing to do */
				break;

			case CHUNK_TYPE_RESET:
				/* next chunk is an ASF header */

				if (instance->chunk_length != 0) {
					/* that's strange, don't know what to do */
					return 0;
				}
				if (!get_header (io, instance))
					return 0;
				interp_header(io, instance);

				/* What todo with: current_pos ??
				Also our chunk_seq_numbers might restart from 0!
				If instance happens with a seekable stream (does it ever?) 
				and we get a seek request after instance were fscked! */
				instance->seekable = 0;

				/* get_header reads the first data packet */
				/* instance->buf_size is set by get_header */
				return 2;

			default:
				lprintf ("mmsh: unexpected chunk type\n");
				return 0;
		}

		len = io_read (io, instance->s, instance->buf, instance->chunk_length);

		if (len == instance->chunk_length) {
			/* explicit padding with 0 */
			if (instance->chunk_length > instance->packet_length) {
				lprintf ("mmsh: chunk_length(%d) > packet_length(%d)\n",
					instance->chunk_length, instance->packet_length);
				return 0;
			}

			{
				char *base	= (char *)(instance->buf);
				char *start = base + instance->chunk_length;
				char *end	= start + instance->packet_length - instance->chunk_length;
				if ((start > base) && (start < (base+CHUNK_SIZE-1)) &&
					(start < end)	&& (end < (base+CHUNK_SIZE-1))) {
						memset(start, 0,
							instance->packet_length - instance->chunk_length);
				}
				if (instance->packet_length > CHUNK_SIZE) {
					instance->buf_size = CHUNK_SIZE;
				} else {
					instance->buf_size = instance->packet_length;
				}
			}
			return 1;
		} else {
			lprintf ("mmsh: read error, %d != %d\n", len, instance->chunk_length);
			return 0;
		}
	} else {
		return 0;
	}
}

int mmsh_peek_header (mmsh_t *instance, char *data, int maxsize) {
	int len;

	lprintf("mmsh_peek_header\n");

	len = ((int) instance->asf_header_len < maxsize) ? (int) instance->asf_header_len : maxsize;

	memcpy(data, instance->asf_header, len);
	return len;
}

int mmsh_read (mms_io_t *io, mmsh_t *instance, char *data, int len) {
	int total;

	total = 0;

	lprintf ("mmsh_read: len: %d\n", len);

	/* Check if the stream didn't get closed because of previous errors */
	if (instance->s == -1)
		return total;

	while (total < len && !g_bStopMmsh) {

		if (instance->asf_header_read < instance->asf_header_len) {
			int n, bytes_left ;

			bytes_left = instance->asf_header_len - instance->asf_header_read ;

			if ((len-total) < bytes_left)
				n = len-total;
			else
				n = bytes_left;

			memcpy (&data[total], &instance->asf_header[instance->asf_header_read], n);

			instance->asf_header_read += n;
			total += n;
			instance->current_pos += n;
		} else {

			int n, bytes_left ;

			bytes_left = instance->buf_size - instance->buf_read;

			if (bytes_left == 0) {
				int packet_type;

				instance->buf_size=instance ->buf_read = 0;
				packet_type = get_media_packet (io, instance);

				if (packet_type == 0) {
					lprintf ("mmsh: get_media_packet failed\n");
					return total;
				} else if (packet_type == 2) {
					continue;
				}
				bytes_left = instance->buf_size;
			}

			if ((len-total) < bytes_left)
				n = len-total;
			else
				n = bytes_left;

			memcpy (&data[total], &instance->buf[instance->buf_read], n);

			instance->buf_read += n;
			total += n;
			instance->current_pos += n;
		}
	}
	return total;
}

mms_off_t mmsh_seek (mms_io_t *io, mmsh_t *instance, mms_off_t offset, int origin) {
	mms_off_t dest;
	mms_off_t dest_packet_seq;
	uint32_t orig_asf_header_len = instance->asf_header_len;
	uint32_t orig_asf_packet_len = instance->packet_length;

	if (!instance->seekable)
		return instance->current_pos;

	switch (origin) {
		case SEEK_SET:
			dest = offset;
			break;
		case SEEK_CUR:
			dest = instance->current_pos + offset;
			break;
		case SEEK_END:
			dest = mmsh_get_length (instance) + offset;
		default:
			return instance->current_pos;
	}

	dest_packet_seq = dest - instance->asf_header_len;
	dest_packet_seq = dest_packet_seq >= 0 ?
		dest_packet_seq / instance->packet_length : -1;

	if (dest_packet_seq < 0) {
		if (instance->chunk_seq_number > 0) {
			lprintf("mmsh: seek within header, already read beyond first packet, resetting connection\n");
			if (!mmsh_connect_int(io, instance, 0, 0)) {
				/* Oops no more connection let our caller know things are fscked up */
				return instance->current_pos = -1;
			}
			/* Some what simple / naive check to check for changed headers
			if the header was changed we are once more fscked up */
			if (instance->asf_header_len != orig_asf_header_len ||
				instance->packet_length	!= orig_asf_packet_len) {
					lprintf("mmsh: AIIEEE asf header or packet length changed on re-open for seek\n");
					/* Its a different stream, so its useless! */
					close (instance->s);
					instance->s = -1;
					return instance->current_pos = -1;
			}
		} else
			lprintf("mmsh: seek within header, resetting buf_read\n");

		// reset buf_read
		instance->buf_read = 0;
		instance->asf_header_read = (uint32_t) dest;
		return instance->current_pos = dest;
	}

	// dest_packet_seq >= 0
	if (instance->asf_num_packets && dest == instance->asf_header_len +
		instance->asf_num_packets*instance->packet_length) {
			// Requesting the packet beyond the last packet, can cause the server to
			// not return any packet or any eos command.	This can cause
			// mms_packet_seek() to hang.
			// This is to allow seeking at end of stream, and avoid hanging.
			--dest_packet_seq;
			lprintf("mmsh: seek to eos!\n");
	}

	if (dest_packet_seq != instance->chunk_seq_number) {

		if (instance->asf_num_packets > 0 && dest_packet_seq >= (int64_t) instance->asf_num_packets) {
			// Do not seek beyond the last packet.
			return instance->current_pos;
		}

		lprintf("mmsh: seek to %d, packet: %d\n", (int)dest, (int)dest_packet_seq);
		if (!mmsh_connect_int(io, instance, (dest_packet_seq+1) * instance->packet_length, 0)) {
			/* Oops no more connection let our caller know things are fscked up */
			return instance->current_pos = -1;
		}
		/* Some what simple / naive check to check for changed headers
		if the header was changed we are once more fscked up */
		if (instance->asf_header_len != orig_asf_header_len ||
			instance->packet_length	!= orig_asf_packet_len) {
				lprintf("mmsh: AIIEEE asf header or packet length changed on re-open for seek\n");
				/* Its a different stream, so its useless! */
				close (instance->s);
				instance->s = -1;
				return instance->current_pos = -1;
		}
	}
	else
		lprintf("mmsh: seek within current packet, dest: %d, current pos: %d\n",
		(int)dest, (int)instance->current_pos);

	/* make sure asf_header is seen as fully read by mmsh_read() instance is needed
	in case our caller tries to seek over part of the header, or when we've
	done an actual packet seek as get_header() resets asf_header_read then. */
	instance->asf_header_read = instance->asf_header_len;

	/* check we got what we want */
	if (dest_packet_seq == instance->chunk_seq_number) {
		instance->buf_read = (int) (dest -
			(instance->asf_header_len + dest_packet_seq*instance->packet_length));
		instance->current_pos = dest;
	} else {
		lprintf("Seek failed, wanted packet: %d, got packet: %d\n",
			(int)dest_packet_seq, (int)instance->chunk_seq_number);
		instance->buf_read = 0;
		instance->current_pos = instance->asf_header_len + instance->chunk_seq_number *
			instance->packet_length;
	}

	lprintf("current_pos after seek to %d: %d (buf_read %d)\n",
		(int)dest, (int)instance->current_pos, (int)instance->buf_read);

	return instance->current_pos;
}

int mmsh_time_seek (mms_io_t *io, mmsh_t *instance, double time_sec) {
	uint32_t orig_asf_header_len = instance->asf_header_len;
	uint32_t orig_asf_packet_len = instance->packet_length;

	if (!instance->seekable)
		return 0;

	lprintf("mmsh: time seek to %f secs\n", time_sec);
	if (!mmsh_connect_int(io, instance, 0, (uint32_t) ((uint64_t) (time_sec * 1000.0) + instance->preroll))) {
		/* Oops no more connection let our caller know things are fscked up */
		instance->current_pos = -1;
		return 0;
	}
	/* Some what simple / naive check to check for changed headers
	if the header was changed we are once more fscked up */
	if (instance->asf_header_len != orig_asf_header_len ||
		instance->packet_length	!= orig_asf_packet_len) {
			lprintf("mmsh: AIIEEE asf header or packet length changed on re-open for seek\n");
			/* Its a different stream, so its useless! */
			close (instance->s);
			instance->s = -1;
			instance->current_pos = -1;
			return 0;
	}

	instance->asf_header_read = instance->asf_header_len;
	instance->buf_read = 0;
	instance->current_pos = instance->asf_header_len + instance->chunk_seq_number *
		instance->packet_length;

	lprintf("mmsh, current_pos after time_seek:%d\n", (int)instance->current_pos);

	return 1;
}

void mmsh_close (mmsh_t *instance) {

	lprintf("mmsh_close\n");

	if (instance->s != -1)
		close(instance->s);
	if (instance->url)
		free(instance->url);
	if (instance->proxy_url)
		free(instance->proxy_url);
	if (instance->proto)
		free(instance->proto);
	if (instance->connect_host)
		free(instance->connect_host);
	if (instance->http_host)
		free(instance->http_host);
	if (instance->proxy_user)
		free(instance->proxy_user);
	if (instance->proxy_password)
		free(instance->proxy_password);
	if (instance->host_user)
		free(instance->host_user);
	if (instance->host_password)
		free(instance->host_password);
	if (instance->uri)
		free(instance->uri);
	if (instance)
		free (instance);
}


uint32_t mmsh_get_length (mmsh_t *instance) {
	/* we could / should return instance->file_len here, but usually instance->file_len
	is longer then the calculation below, as usually an asf file contains an
	asf index object after the data stream. However since we do not have a
	(known) way to get to instance index object through mms, we return a
	calculated size of what we can get to when we know. */
	if (instance->asf_num_packets)
		return instance->asf_header_len + (uint32_t) (instance->asf_num_packets*instance->packet_length);
	else
		return (uint32_t) instance->file_length;
}

double mmsh_get_time_length (mmsh_t *instance) {
	return (double)(instance->time_len) / 1e7;
}

uint64_t mmsh_get_raw_time_length (mmsh_t *instance) {
	return instance->time_len;
}

mms_off_t mmsh_get_current_pos (mmsh_t *instance) {
	return instance->current_pos;
}

uint32_t mmsh_get_asf_header_len (mmsh_t *instance) {
	return instance->asf_header_len;
}

uint32_t mmsh_get_asf_packet_len (mmsh_t *instance) {
	return instance->packet_length;
}

int mmsh_get_seekable (mmsh_t *instance) {
	return instance->seekable;
}

void mmsh_stop(mmsh_t* instance)
{
	g_bStopMmsh = 1;
}
