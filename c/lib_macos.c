#include <stdio.h>
#include <sys/socket.h>
#include <sys/event.h>
#include <netinet/in.h>
#include <netinet/tcp.h>
#include <arpa/inet.h>
#include <fcntl.h>
#include <string.h>
#include <stdlib.h>
#include <unistd.h>
#include <errno.h>
#include "share.h"
#include "lib_macos.h"

int m_connect_block_code()
{
    return EINPROGRESS;
}

int m_send_block_code()
{
    return EAGAIN;
}

int m_interrupt_code()
{
    return EINTR;
}

int m_ipv4_address_len()
{
    return INET_ADDRSTRLEN;
}

int m_ipv6_address_len()
{
    return INET6_ADDRSTRLEN;
}

int m_ipv4_address_size()
{
    return sizeof(struct sockaddr_in);
}

int m_ipv6_address_size()
{
    return sizeof(struct sockaddr_in6);
}

int m_ipv4_address_align()
{
    return _Alignof(struct sockaddr_in);
}

int m_ipv6_address_align()
{
    return _Alignof(struct sockaddr_in6);
}

int m_kqueue()
{
    int r = kqueue();
    if (unlikely(r == -1))
    {
        return -errno;
    }
    return r;
}

int m_kevent_ctl(int kq, struct kevent *changelist, int nchanges)
{
    int r = kevent(kq, changelist, nchanges, NULL, 0, NULL);
    if (unlikely(r == -1))
    {
        return -errno;
    }
    return r;
}

int m_kevent_wait(int kq, struct kevent *eventlist, int nevents, int timeout)
{
    struct timespec t = {.tv_sec = timeout / 1000, .tv_nsec = (timeout % 1000) * 1000000};
    int r = kevent(kq, NULL, 0, eventlist, nevents, &t);
    if (unlikely(r == -1))
    {
        return -errno;
    }
    return r;
}

int m_get_ipv4_address(struct sockaddr_in *sockAddr, char *addrStr, socklen_t len)
{
    const char *result = inet_ntop(AF_INET, &(sockAddr->sin_addr), addrStr, len);
    if (unlikely(result == NULL))
    {
        return -errno;
    }
    return 0;
}

int m_get_ipv6_address(struct sockaddr_in6 *sockAddr, char *addrStr, socklen_t len)
{
    const char *result = inet_ntop(AF_INET6, &(sockAddr->sin6_addr), addrStr, len);
    if (unlikely(result == NULL))
    {
        return -errno;
    }
    return 0;
}

uint16_t m_ipv4_port(struct sockaddr_in *sockAddr)
{
    return ntohs(sockAddr->sin_port);
}

uint16_t m_ipv6_port(struct sockaddr_in6 *sockAddr)
{
    return ntohs(sockAddr->sin6_port);
}

int m_ipv4_socket_create()
{
    int r = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);
    if (unlikely(r == -1))
    {
        return -errno;
    }
    return r;
}

int m_ipv6_socket_create()
{
    int r = socket(AF_INET6, SOCK_STREAM, IPPROTO_TCP);
    if (unlikely(r == -1))
    {
        return -errno;
    }
    return r;
}

int m_set_ipv4_sock_addr(struct sockaddr_in *sockAddr, char *address, uint16_t port)
{
    sockAddr->sin_family = AF_INET;
    sockAddr->sin_port = htons(port);
    if (address == NULL)
    {
        sockAddr->sin_addr.s_addr = INADDR_ANY;
        return 1;
    }
    else
    {
        int r = inet_pton(AF_INET, address, &(sockAddr->sin_addr));
        if (unlikely(r == -1))
        {
            return -errno;
        }
        return r;
    }
}

int m_set_ipv6_sock_addr(struct sockaddr_in6 *sockAddr, char *address, uint16_t port)
{
    sockAddr->sin6_family = AF_INET6;
    sockAddr->sin6_port = htons(port);
    if (address == NULL)
    {
        sockAddr->sin6_addr = in6addr_any;
        return 1;
    }
    else
    {
        int r = inet_pton(AF_INET6, address, &(sockAddr->sin6_addr));
        if (unlikely(r == -1))
        {
            return -errno;
        }
        return r;
    }
}

int m_set_reuse_addr(int socket, int value)
{
    int r = setsockopt(socket, SOL_SOCKET, SO_REUSEADDR, (void *)&value, sizeof(value));
    if (unlikely(r == -1))
    {
        return -errno;
    }
    return r;
}

int m_set_keep_alive(int socket, int value)
{
    int r = setsockopt(socket, SOL_SOCKET, SO_KEEPALIVE, (void *)&value, sizeof(value));
    if (unlikely(r == -1))
    {
        return -errno;
    }
    return r;
}

int m_set_tcp_no_delay(int socket, int value)
{
    int r = setsockopt(socket, IPPROTO_TCP, TCP_NODELAY, (void *)&value, sizeof(value));
    if (unlikely(r == -1))
    {
        return -errno;
    }
    return r;
}

int m_set_ipv6_only(int socket, int value)
{
    int r = setsockopt(socket, IPPROTO_IPV6, IPV6_V6ONLY, (void *)&value, sizeof(value));
    if (unlikely(r == -1))
    {
        return -errno;
    }
    return r;
}

int m_get_err_opt(int socket, int *ptr)
{
    socklen_t ptr_size = sizeof(int);
    int r = getsockopt(socket, SOL_SOCKET, SO_ERROR, (void *)ptr, &ptr_size);
    if (unlikely(r == -1))
    {
        return -errno;
    }
    return r;
}

int m_set_nonblocking(int socket)
{
    int flag = fcntl(socket, F_GETFD, 0);
    int r = fcntl(socket, F_SETFL, flag | O_NONBLOCK);
    if (unlikely(r == -1))
    {
        return -errno;
    }
    return r;
}

int m_bind(int socket, void *sockAddr, int size)
{
    int r = bind(socket, (struct sockaddr *)sockAddr, size);
    if (unlikely(r == -1))
    {
        return -errno;
    }
    return r;
}

int m_listen(int socket, int backlog)
{
    int r = listen(socket, backlog < SOMAXCONN ? backlog : SOMAXCONN);
    if (unlikely(r == -1))
    {
        return -errno;
    }
    return r;
}

int m_connect(int socket, void *sockAddr, int size)
{
    int r = connect(socket, (struct sockaddr *)sockAddr, size);
    if (likely(r == -1))
    {
        return -errno;
    }
    return r;
}

int m_accept(int socket, void *clientAddr, socklen_t clientAddrSize)
{
    int r = accept(socket, (struct sockaddr *)clientAddr, &clientAddrSize);
    if (unlikely(r == -1))
    {
        return -errno;
    }
    return r;
}

ssize_t m_recv(int socket, void *buf, size_t len)
{
    int r = recv(socket, buf, len, 0);
    if (unlikely(r == -1))
    {
        return -errno;
    }
    return r;
}

ssize_t m_send(int socket, void *buf, size_t len)
{
    int r = send(socket, buf, len, 0);
    if (unlikely(r == -1))
    {
        return -errno;
    }
    return r;
}

int m_shutdown_write(int fd)
{
    int r = shutdown(fd, SHUT_WR);
    if (unlikely(r == -1))
    {
        return -errno;
    }
    return r;
}

int m_close(int fd)
{
    int r = close(fd);
    if (unlikely(r == -1))
    {
        return -errno;
    }
    return r;
}
