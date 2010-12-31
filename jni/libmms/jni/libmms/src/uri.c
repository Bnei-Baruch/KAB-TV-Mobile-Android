/* GNet - Networking library
 * Copyright (C) 2000-2003	David Helder, David Bolcsfoldi, Eric Williams
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the 
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA	02111-1307, USA.
 */

#include <unistd.h>
#include <string.h>
#include <stdlib.h>
#include <ctype.h>
#include <stdio.h>

#include "uri.h"
#include <string.h>

static void field_unescape (char *str);
static char* field_escape (char* str, unsigned char mask);

#define USERINFO_ESCAPE_MASK		0x01
#define PATH_ESCAPE_MASK			0x02
#define QUERY_ESCAPE_MASK			0x04
#define FRAGMENT_ESCAPE_MASK		0x08

static unsigned char neednt_escape_table[] = 
{
		0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 
		0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 
		0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 
		0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 
		0x00, 0x0f, 0x00, 0x00, 0x0f, 0x00, 0x0f, 0x0f, 
		0x0f, 0x0f, 0x0f, 0x0f, 0x0f, 0x0f, 0x0f, 0x0e, 
		0x0f, 0x0f, 0x0f, 0x0f, 0x0f, 0x0f, 0x0f, 0x0f, 
		0x0f, 0x0f, 0x0f, 0x0f, 0x00, 0x0f, 0x00, 0x0c, 
		0x0e, 0x0f, 0x0f, 0x0f, 0x0f, 0x0f, 0x0f, 0x0f, 
		0x0f, 0x0f, 0x0f, 0x0f, 0x0f, 0x0f, 0x0f, 0x0f, 
		0x0f, 0x0f, 0x0f, 0x0f, 0x0f, 0x0f, 0x0f, 0x0f, 
		0x0f, 0x0f, 0x0f, 0x00, 0x0f, 0x00, 0x00, 0x0f, 
		0x00, 0x0f, 0x0f, 0x0f, 0x0f, 0x0f, 0x0f, 0x0f, 
		0x0f, 0x0f, 0x0f, 0x0f, 0x0f, 0x0f, 0x0f, 0x0f, 
		0x0f, 0x0f, 0x0f, 0x0f, 0x0f, 0x0f, 0x0f, 0x0f, 
		0x0f, 0x0f, 0x0f, 0x00, 0x00, 0x00, 0x0f, 0x00, 
		0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 
		0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 
		0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 
		0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 
		0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 
		0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 
		0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 
		0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 
		0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 
		0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 
		0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 
		0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 
		0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 
		0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 
		0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 
		0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00
};


/*
Perl code to generate above table:

#!/usr/bin/perl

$ok = "abcdefghijklmnopqrstuvwxyz" . 
			"ABCDEFGHIJKLMNOPQRSTUVWXYZ" .
			"0123456789" .
			"-_.!~*'()";
$userinfo_ok = ';:&=+\$,';
$path_ok		 = ':\@&=+\$,;/';
$query_ok		= ';/?:\@&=+\$,';
$fragment_ok = ';/?:\@&=+\$,';

for ($i = 0; $i < 32; $i++)
{
		print "	";
		for ($j = 0; $j < 8; $j++)
		{
		$num = 0;
		$letter = chr(($i * 8) + $j);

		$num |= 0b0001	if (index($userinfo_ok, $letter) != -1);
		$num |= 0b0010	if (index($path_ok,		 $letter) != -1);
		$num |= 0b0100	if (index($query_ok,		$letter) != -1);
		$num |= 0b1000	if (index($fragment_ok, $letter) != -1);
		$num |= 0b1111	if (index($ok,					$letter) != -1);

		printf "0x%02x, ", $num;
		}
		print "\n";
}
*/


/* our own ISSPACE.	ANSI isspace is local dependent */
#define ISSPACE(C) (((C) >= 9 && (C) <= 13) || (C) == ' ')


static int split_user_passwd(const char* in, char** user, char** passwd)
{
	char *tmp = strdup(in);

	if(!tmp)
		return 0;
	*passwd = strchr(tmp, ':');
	if(!(*passwd))
	{
		free(tmp);
		return 0;
	}
	*((*passwd)++) = '\0'; // don't you love C? :)

	*user = strdup(tmp);
	if(!*user)
		return 0;
	*passwd = strdup(*passwd);
	if(!*passwd)
		return 0;

	free(tmp);
	return 1;
}

static unsigned int str_hash(const unsigned char *str)
{
	unsigned int hash = 5381;
	int c;

	while (c = *str++)
		hash = ((hash << 5) + hash) + c; /* hash * 33 + c */

	return hash;
}

static void string_append(char* were, const char* what, int* pnleft)
{
	int n_were = 0, n_what = 0;
	if(were && what && pnleft && (*pnleft > 0))
	{
		n_were = strlen(were);
		n_what = strlen(what);
		// Leave 1 char for \0
		if(n_what < *pnleft)
		{
				strncpy(were + n_were, what, n_what);
				*pnleft = *pnleft - n_what;
		} else
		{
			*pnleft = -1;
		}
	}
}

/**
 *	gnet_uri_new
 *	@uri: URI string
 *
 *	Creates a #GURI from a string.	Empty fields are set to NULL.	The
 *	parser does not validate the URI -- it will accept some malformed
 *	URI.	URIs are usually in the form
 *	scheme://userinfo@hostname:port/path?query#fragment
 *
 *	URIs created from user input are typically unescaped.	URIs
 *	created from machine input (e.g. received over the internet) are
 *	typically escaped.
 *	
 *	Returns: a new #GURI, or NULL if there was a failure.
 *
 **/
GURI* 
gnet_uri_new (const char* uri)
{
	GURI* guri = NULL;
	const char* p;
	const char* temp;

	if (!uri)
		return NULL;

	/* Skip initial whitespace */
	p = uri;
	while (*p && ISSPACE((int)*p))
		++p;
	if (!*p)		/* Error if it's just a string of space */
		return NULL;

	guri = calloc(1, sizeof(GURI));

	/* Scheme */
	temp = p;
	while (*p && *p != ':' && *p != '/' && *p != '?' && *p != '#')
		++p;
	if (*p == ':')
		{
			guri->scheme = strndup (temp, p - temp);
			++p;
		}
	else		/* This char is NUL, /, ?, or # */
		p = temp;

	/* Authority */
	if (*p == '/' && p[1] == '/')
		{
			char *userinfo;
			p += 2;

			/* Userinfo */
			temp = p;
			while (*p && *p != '@' && *p != '/' ) /* Look for @ or / */
		++p;
			if (*p == '@') /* Found userinfo */
		{
			userinfo = strndup (temp, p - temp);
			if(!split_user_passwd(userinfo, &guri->user, &guri->passwd))
			{
				free(userinfo);
				goto error;
			}
			free(userinfo);
			++p;
		}
			else
		p = temp;

			/* Hostname */

			/* Check for IPv6 canonical hostname in brackets */
			if (*p == '[')
		{
			p++;	/* Skip [ */
			temp = p;
			while (*p && *p != ']') ++p;
			if ((p - temp) == 0)
				goto error;
			guri->hostname = strndup (temp, p - temp);
			if (*p)
				p++;		/* Skip ] (if there) */
		}
			else
		{
			temp = p;
			while (*p && *p != '/' && *p != '?' && *p != '#' && *p != ':') ++p;
			if ((p - temp) == 0) 
				goto error;
			guri->hostname = strndup (temp, p - temp);
		}

			/* Port */
			if (*p == ':')
		{
			for (++p; isdigit((int)*p); ++p)
				guri->port = guri->port * 10 + (*p - '0');
		}

		}

	/* Path (we are liberal and won't check if it starts with /) */
	temp = p;
	while (*p && *p != '?' && *p != '#')
		++p;
	if (p != temp)
		guri->path = strndup(temp, p - temp);

	/* Query */
	if (*p == '?')
		{
			temp = p + 1;
			while (*p && *p != '#')
				++p;
			guri->query = strndup (temp, p - temp);
		}

	/* Fragment */
	if (*p == '#')
		{
			++p;
			guri->fragment = strdup (p);
		}

	return guri;

 error:
	gnet_uri_delete (guri);
	return NULL;
}


/**
 *	gnet_uri_new_fields
 *	@scheme: scheme
 *	@hostname: host name
 *	@port: port
 *	@path: path
 *
 *	Creates a #GURI from the fields.	This function uses the most
 *	common fields.	Use gnet_uri_new_fields_all() to specify all
 *	fields.
 *
 *	Returns: a new #GURI.
 *
 **/
GURI*		 
gnet_uri_new_fields (const char* scheme, const char* hostname, 
						 const int port, const char* path)
{
	GURI* uri = NULL;

	uri = calloc(1, sizeof(GURI));
	if (scheme)
		uri->scheme = strdup (scheme);
	if (hostname)
		uri->hostname = strdup (hostname);
	uri->port = port;
	if (path)
		uri->path = strdup (path);

	return uri;
}


/**
 *	gnet_uri_new_fields_all
 *	@scheme: scheme
 *	@userinfo: user info
 *	@hostname: host name
 *	@port: port
 *	@path: path
 *	@query: query
 *	@fragment: fragment
 *
 *	Creates a #GURI from all fields.
 *
 *	Returns: a new #GURI.
 *
 **/
GURI*
gnet_uri_new_fields_all (const char* scheme, const char* user, 
						 const char* passwd, const char* hostname,
						 const int port, const char* path, 
						 const char* query, const char* fragment)
{
	GURI* uri = NULL;

	uri = calloc(1, sizeof(GURI));
	if (scheme)
		uri->scheme = strdup (scheme);
	if (user)
		uri->user = strdup (user);
	if (passwd)
		uri->passwd	 = strdup (passwd);
	if (hostname)
		uri->hostname = strdup (hostname);
	uri->port = port;
	if (path)
		uri->path = strdup (path);
	if (query)
		uri->query = strdup (query);
	if (fragment)
		uri->fragment = strdup (fragment);

	return uri;
}


/**
 *	gnet_uri_clone:
 *	@uri: a #GURI
 * 
 *	Copies a #GURI.
 *
 *	Returns: a copy of @uri.
 *
 **/
GURI*		 
gnet_uri_clone (const GURI* uri)
{
	GURI* uri2;

	if (!uri)
		return NULL;

	uri2 = calloc(1, sizeof(GURI));
	if(uri->scheme)
		uri2->scheme = strdup(uri->scheme);
	if(uri->user)
		uri2->user = strdup(uri->user);
	if(uri->passwd)
		uri2->passwd = strdup(uri->passwd);
	if(uri->hostname)
		uri2->hostname = strdup(uri->hostname);
	uri2->port = uri->port;
	if(uri->path)
		uri2->path = strdup(uri->path);
	if(uri->query)
		uri2->query = strdup(uri->query);
	if(uri->fragment)
		uri2->fragment = strdup(uri->fragment);

	return uri2;
}


/** 
 *	gnet_uri_delete:
 *	@uri: a #GURI
 *
 *	Deletes a #GURI.
 *
 **/
void
gnet_uri_delete (GURI* uri)
{
	if (uri)
		{
			free (uri->scheme);
			free (uri->user);
			free (uri->passwd);
			free (uri->hostname);
			free (uri->path);
			free (uri->query);
			free (uri->fragment);
			free (uri);
		}
}




#define SAFESTRCMP(A,B) (((A)&&(B))?(strcmp((A),(B))):((A)||(B)))

/**
 *	gnet_uri_equal
 *	@p1: a #GURI
 *	@p2: another #GURI
 *
 *	Compares two #GURI's for equality.
 *
 *	Returns: TRUE if they are equal; FALSE otherwise.
 *
 **/
int
gnet_uri_equal (const void* p1, const void* p2)
{
	const GURI* uri1 = (const GURI*) p1;
	const GURI* uri2 = (const GURI*) p2;

	if (!uri1 || !uri2)
		return 0;

	if (uri1->port == uri2->port &&
			!SAFESTRCMP(uri1->scheme, uri2->scheme) &&
			!SAFESTRCMP(uri1->user, uri2->user) &&
			!SAFESTRCMP(uri1->passwd, uri2->passwd) &&
			!SAFESTRCMP(uri1->hostname, uri2->hostname) &&
			!SAFESTRCMP(uri1->path, uri2->path) &&
			!SAFESTRCMP(uri1->query, uri2->query) &&
			!SAFESTRCMP(uri1->fragment, uri2->fragment))
		return 1;

	return 0;
}


/**
 *	gnet_uri_hash
 *	@p: a #GURI
 *
 *	Creates a hash code for @p for use with GHashTable. 
 *
 *	Returns: hash code for @p.
 *
 **/
unsigned int
gnet_uri_hash (const void* p)
{
	const GURI* uri = (const GURI*) p;
	unsigned int h = 0;

	if (!uri)
		return 0;

	if (uri->scheme)		h =	str_hash (uri->scheme);
	if (uri->user)		h ^= str_hash (uri->user);
	if (uri->passwd)		h ^= str_hash (uri->passwd);
	if (uri->hostname)		h ^= str_hash (uri->hostname);
	h ^= uri->port;
	if (uri->path)		h ^= str_hash (uri->path);
	if (uri->query)		h ^= str_hash (uri->query);
	if (uri->fragment)		h ^= str_hash (uri->fragment);
	
	return h;
}


/**
 *	gnet_uri_escape
 *	@uri: a #GURI
 *
 *	Escapes the fields in a #GURI.	Network protocols use escaped
 *	URIs.	People use unescaped URIs.
 *
 **/
void
gnet_uri_escape (GURI* uri)
{
	if (!uri)
		return;
	
	uri->user = field_escape (uri->user, USERINFO_ESCAPE_MASK);
	uri->passwd = field_escape (uri->passwd, USERINFO_ESCAPE_MASK);
	uri->path		 = field_escape (uri->path,		 PATH_ESCAPE_MASK);
	uri->query		= field_escape (uri->query,		QUERY_ESCAPE_MASK);
	uri->fragment = field_escape (uri->fragment, FRAGMENT_ESCAPE_MASK);
}


/**
 *	gnet_uri_unescape
 *	@uri: a #GURI
 *
 *	Unescapes the fields in the URI.	Network protocols use escaped
 *	URIs.	People use unescaped URIs.
 *
 **/
void
gnet_uri_unescape (GURI* uri)
{
	if (!uri)
		return;

	if (uri->user)
		field_unescape (uri->user);
	if (uri->passwd)
		field_unescape (uri->passwd);
	if (uri->path)
		field_unescape (uri->path);
	if (uri->query)
		field_unescape (uri->query);
	if (uri->fragment)
		field_unescape (uri->fragment);
}


static char*
field_escape (char* str, unsigned char mask)
{
	int len;
	int i;
	int must_escape = 0;
	char* dst;
	int j;

	if (str == NULL)
		return NULL;

	/* Roughly calculate buffer size */
	len = 0;
	for (i = 0; str[i]; i++)
		{
			if (neednt_escape_table[(unsigned int) str[i]] & mask)
		len++;
			else
		{
			len += 3;
			must_escape = 1;
		}
		}

	/* Don't escape if unnecessary */
	if (must_escape == 0)
		return str;
		
	/* Allocate buffer */
	dst = (char*) calloc(len + 1, 1);

	/* Copy */
	for (i = j = 0; str[i]; i++, j++)
		{
			/* Unescaped character */
			if (neednt_escape_table[(unsigned int) str[i]] & mask)
		{
			dst[j] = str[i];
		}

			/* Escaped character */
			else
		{
			dst[j] = '%';

			if (((str[i] & 0xf0) >> 4) < 10)
				dst[j+1] = ((str[i] & 0xf0) >> 4) + '0';
			else
				dst[j+1] = ((str[i] & 0xf0) >> 4) + 'a' - 10;

			if ((str[i] & 0x0f) < 10)
				dst[j+2] = (str[i] & 0x0f) + '0';
			else
				dst[j+2] = (str[i] & 0x0f) + 'a' - 10;

			j += 2;	/* and j is incremented in loop too */
		}
		}
	dst[j] = '\0';

	free (str);
	return dst;
}



static void
field_unescape (char* s)
{
	char* src;
	char* dst;

	for (src = dst = s; *src; ++src, ++dst)
		{
			if (src[0] == '%' && src[1] != '\0' && src[2] != '\0')
		{
			unsigned int high, low;

			if ('a' <= src[1] && src[1] <= 'f')
				high = src[1] - 'a' + 10;
			else if ('A' <= src[1] && src[1] <= 'F')
				high = src[1] - 'A' + 10;
			else if ('0' <= src[1] && src[1] <= '9')
				high = src[1] - '0';
			else	/* malformed */
				goto regular_copy;

			if ('a' <= src[2] && src[2] <= 'f')
				low = src[2] - 'a' + 10;
			else if ('A' <= src[2] && src[2] <= 'F')
				low = src[2] - 'A' + 10;
			else if ('0' <= src[2] && src[2] <= '9')
				low = src[2] - '0';
			else	/* malformed */
				goto regular_copy;

			*dst = (char)((high << 4) + low);
			src += 2;
		}
			else
		{
		regular_copy:
			*dst = *src;
		}
		}

	*dst = '\0';
}



/**
 *	gnet_uri_get_string
 *	@uri: a #GURI
 *
 *	Gets a string representation of a #GURI.	This function does not
 *	escape or unescape the fields first.	Call gnet_uri_escape() or
 *	gnet_uri_unescape first if necessary.
 *
 *	Returns: a string.
 *
 **/
char*
gnet_uri_get_string (const GURI* uri)
{
	char* rv = NULL;
	int n = 1024;
	char* buffer = NULL;
	char tmp[32] = {0};

	if (!uri)
	 return 0;

	buffer = calloc (n, 4);

	if (uri->scheme)
	{
		string_append (buffer, uri->scheme, &n);
		string_append (buffer, ":", &n);
	}

	if (uri->user || uri->passwd || uri->hostname || uri->port)
		string_append (buffer, "//", &n);

	if (uri->user)
		{
			string_append (buffer, uri->user, &n);
			string_append (buffer, "@", &n);
		}
	if (uri->passwd)
		{
			string_append (buffer, uri->passwd, &n);
			string_append (buffer, "@", &n);
		}

	/* Add brackets around the hostname if it's IPv6 */
	if (uri->hostname)
		{
			if (strchr(uri->hostname, ':') == NULL) 
				string_append (buffer, uri->hostname, &n); 
			else
			{
				string_append (buffer, "[", &n);
				string_append (buffer, uri->hostname, &n);
				string_append (buffer, "]", &n);
			}
		}

	if (uri->port)
	{
		string_append (buffer, ":", &n);
		snprintf(tmp, sizeof(tmp) - 1, "%i", uri->port);
		string_append (buffer, tmp, &n);
	}

	if (uri->path)
		{
			if (*uri->path == '/' ||
				!(uri->user || uri->passwd || uri->hostname || uri->port))
			{
				string_append (buffer, uri->path, &n);
			}
			else
			{
				string_append (buffer, "/", &n);
				string_append (buffer, uri->path, &n);
			}
		}

	if (uri->query)
	{
		string_append (buffer, "?", &n);
		string_append (buffer, uri->query, &n);
	}

	if (uri->fragment)
	{
		string_append (buffer, "#", &n);
		string_append (buffer, uri->fragment, &n);
	}

	if(n < 0)
	{
		free(buffer);
		return 0;
	} else
		return buffer;
}


/**
 *	gnet_uri_set_scheme
 *	@uri: a #GURI
 *	@scheme: scheme
 *
 *	Sets a #GURI's scheme.
 *
 **/
void
gnet_uri_set_scheme (GURI* uri, const char* scheme)
{
	if (!uri)
		return;

	if (uri->scheme)
		{
			free (uri->scheme);
			uri->scheme = NULL;
		}

	if (scheme)
		uri->scheme = strdup (scheme);
}


/**
 *	gnet_uri_set_userinfo
 *	@uri: a #GURI
 *	@userinfo: user info
 *
 *	Sets a #GURI's user info.
 *
 **/
void
gnet_uri_set_userinfo (GURI* uri, const char* user, const char* passwd)
{
	if(!uri)
		return;

	if (uri->user)
		{
			free (uri->user);
			uri->user = NULL;
		}
	if (uri->passwd)
		{
			free (uri->passwd);
			uri->passwd = NULL;
		}

	if (user)
		uri->user = strdup (user);
	if (passwd)
		uri->passwd = strdup (passwd);
}


/**
 *	gnet_uri_set_hostname
 *	@uri: a #GURI
 *	@hostname: host name
 *
 *	Sets a #GURI's host name.
 *
 **/
void
gnet_uri_set_hostname (GURI* uri, const char* hostname)
{
	if (!uri)
		return;

	if (uri->hostname)
		{
			free (uri->hostname);
			uri->hostname = NULL;
		}

	if (hostname)
		uri->hostname = strdup (hostname);
}


/**
 *	gnet_uri_set_port
 *	@uri: a #GURI
 *	@port: port
 *
 *	Set a #GURI's port.
 *
 **/
void		
gnet_uri_set_port (GURI* uri, int port)
{
	uri->port = port;
}


/**
 *	gnet_uri_set_path
 *	@uri: a #GURI
 *	@path: path
 *
 *	Set a #GURI's path.
 *
 **/
void
gnet_uri_set_path (GURI* uri, const char* path)
{
	if (!uri)
		return;

	if (uri->path)
		{
			free (uri->path);
			uri->path = NULL;
		}

	if (path)
		uri->path = strdup (path);
}



/**
 *	gnet_uri_set_query
 *	@uri: a #GURI
 *	@query: query
 *
 *	Set a #GURI's query.
 *
 **/
void
gnet_uri_set_query (GURI* uri, const char* query)
{
	if (!uri)
		return;

	if (uri->query)
		{
			free (uri->query);
			uri->query = NULL;
		}

	if (query)
		uri->query = strdup (query);
}


/**
 *	gnet_uri_set_fragment
 *	@uri: a #GURI
 *	@fragment: fragment
 *
 *	Set a #GURI's fragment.
 *
 **/
void
gnet_uri_set_fragment (GURI* uri, const char* fragment)
{
	if (!uri)
		return;

	if (uri->fragment)
		{
			free (uri->fragment);
			uri->fragment = NULL;
		}

	if (fragment)
		uri->fragment = strdup (fragment);
}


/**
 *	gnet_mms_helper
 *	@uri: a #GURI
 *
 *	returns char* representation of an uri that is sutable for
 *	using in mms protocol.
 *	'/path?query'
 *
 **/

char* gnet_mms_helper(const GURI* uri)
{
	size_t len = 0;
	char *ret, *tmp = NULL;


	/* Strip leading slashes and calculate the length of the path
	 * which might not be present in the URI */
	if (uri->path) {
			tmp = uri->path;
			while (*tmp == '/')
					++tmp;
			len += strlen(tmp);
	}
	/* Append length of the query part */
	if (uri->query)
			len += strlen(uri->query) + 1; /* + '?' */

	if (!(ret = (char*) calloc(len + 2, 1)))
		return NULL;

	strcpy(ret, "/");

	/* Copy the optional path */
	if (tmp)
			strcat(ret, tmp);

	/* Copy the optional query */
	if (uri->query) {
		strcat(ret, "?");
		strcat(ret, uri->query);
	}

	return ret;
}
