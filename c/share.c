#include <stdio.h>
#include "share.h"
#include "rpmalloc.h"

FILE *get_stdout()
{
    return stdout;
}

FILE *get_stderr()
{
    return stderr;
}

int get_fbf()
{
    return _IOFBF;
}

int get_lbf()
{
    return _IOLBF;
}

int get_nbf()
{
    return _IONBF;
}

int rp_initialize()
{
    return rpmalloc_initialize();
}

void rp_tinitialize()
{
    rpmalloc_thread_initialize();
}

void rp_tfinalize()
{
    rpmalloc_thread_finalize(1);
}

void rp_finalize()
{
    rpmalloc_finalize();
}

void *rp_malloc(size_t size)
{
    return rpmalloc(size);
}

void rp_free(void *ptr)
{
    rpfree(ptr);
}

void *rp_realloc(void *ptr, size_t size)
{
    return rprealloc(ptr, size);
}