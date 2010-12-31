/*
 * Copyright (C) 2007 Hans de Goede <j.w.r.degoede@hhs.nl>
 *
 * This file is part of libmms a free mms protocol library
 *
 * libmms is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Library General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * libmss is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA	02111-1307, USA
 */

/*
 * instance is a small wrapper around the mms and mmsh protocol implementations
 * in libmms. The instance functions provide transparent access to both protocols
 * so that programs who wish to support both can do so with a single code path
 * if desired.
 */

#include <stdlib.h>
#include "mmsx.h"
#include "mms.h"
#include "mmsh.h"
#include <time.h>
#include <string.h>

struct mmsx_s {
	mms_t *connection;
	mmsh_t *connection_h;
};

int g_bStop = 0;

mmsx_t* mmsx_connect(mms_io_t *io, void *data, const char *url, int bandwidth)
{
	mmsx_t* instance = calloc(1, sizeof(mmsx_t));
	int i = 0;
	time_t t = 0;

	if (!instance)
		return instance;

	g_bStop = 0;

	instance->connection = mms_connect(io, data, url, bandwidth);
	if (instance->connection)
		return instance;

	if(!g_bStop)
	{
		instance->connection_h = mmsh_connect(io, data, url, bandwidth);
		if (instance->connection_h)
			return instance;
	}

	free(instance);
	return NULL;
}

int mmsx_read (mms_io_t *io, mmsx_t* instance, char *data, int len)
{
	if(instance)
	{
		if(instance->connection)
			return mms_read(io, instance->connection, data, len);
		if(instance->connection_h)
			return mmsh_read(io, instance->connection_h, data, len);
	}
	return 0;
}

int mmsx_time_seek (mms_io_t *io, mmsx_t* instance, double time_sec)
{
	if(instance)
	{
		if(instance->connection)
			return mms_time_seek(io, instance->connection, time_sec);
		if(instance->connection_h)
			return mmsh_time_seek(io, instance->connection_h, time_sec);
	}
	return 0;
}

mms_off_t mmsx_seek (mms_io_t *io, mmsx_t* instance, mms_off_t offset, int origin)
{
	if(instance)
	{
		if(instance->connection)
			return mms_seek(io, instance->connection, offset, origin);
		if(instance->connection_h)
			return mmsh_seek(io, instance->connection_h, offset, origin);
	}
	return 0;
}

double mmsx_get_time_length (mmsx_t* instance)
{
	if(instance)
	{
		if(instance->connection)
			return mms_get_time_length(instance->connection);
		if(instance->connection_h)
			return mmsh_get_time_length(instance->connection_h);
	}
	return 0;
}

uint64_t mmsx_get_raw_time_length (mmsx_t* instance)
{
	if(instance)
	{
		if(instance->connection)
			return mms_get_raw_time_length(instance->connection);
		if(instance->connection_h)
			return mmsh_get_raw_time_length(instance->connection_h);
	}
	return 0;
}

uint32_t mmsx_get_length (mmsx_t* instance)
{
	if(instance)
	{
		if(instance->connection)
			return mms_get_length(instance->connection);
		if(instance->connection_h)
			return mmsh_get_length(instance->connection_h);
	}
	return 0;
}

void mmsx_close (mmsx_t* instance)
{
	if(instance)
	{
		if(instance->connection)
			mms_close(instance->connection);
		if(instance->connection_h)
			mmsh_close(instance->connection_h);

		free(instance);
	}
}

int mmsx_peek_header (mmsx_t* instance, char *data, int maxsize)
{
	if(instance)
	{
		if(instance->connection)
			return mms_peek_header(instance->connection, data, maxsize);
		if(instance->connection_h)
			return mmsh_peek_header(instance->connection_h, data, maxsize);
	}
	return 0;
}

mms_off_t mmsx_get_current_pos (mmsx_t* instance)
{
	if(instance)
	{
		if(instance->connection)
			return mms_get_current_pos(instance->connection);
		if(instance->connection_h)
			return mmsh_get_current_pos(instance->connection_h);
	}
	return 0;
}

uint32_t mmsx_get_asf_header_len(mmsx_t* instance)
{
	if(instance)
	{
		if(instance->connection)
			return mms_get_asf_header_len(instance->connection);
		if(instance->connection_h)
			return mmsh_get_asf_header_len(instance->connection_h);
	}
	return 0;
}

uint64_t mmsx_get_asf_packet_len (mmsx_t* instance)
{
	if(instance)
	{
		if(instance->connection)
			return mms_get_asf_packet_len(instance->connection);
		if(instance->connection_h)
			return mmsh_get_asf_packet_len(instance->connection_h);
	}
	return 0;
}

int mmsx_get_seekable (mmsx_t* instance)
{
	if(instance)
	{
		if(instance->connection)
			return mms_get_seekable(instance->connection);
		if(instance->connection_h)
			return mmsh_get_seekable(instance->connection_h);
	}
	return 0;
}

void mmsx_stop(mmsx_t* instance)
{
	g_bStop = 0;
	if(instance)
	{
		if(instance->connection)
			mms_stop(instance->connection);
		if(instance->connection_h)
			mmsh_stop(instance->connection_h);
	}
}
