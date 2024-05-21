#include <stdint.h>
#include <sys/epoll.h>
#include <netinet/in.h>
#include <sys/socket.h>
#include <netinet/tcp.h>
#include <arpa/inet.h>
#include <unistd.h>
#include <errno.h>
#include <fcntl.h>
#include <string.h>
#include <stdbool.h>
#include <stdlib.h>
#include "share.h"
#include "lib_linux.h"

int l_connect_block_code()
{
    return EINPROGRESS;
}

int l_send_block_code()
{
    return EAGAIN;
}

int l_interrupt_code()
{
    return EINTR;
}

int l_ipv4_address_len()
{
    return INET_ADDRSTRLEN;
}

int l_ipv6_address_len()
{
    return INET6_ADDRSTRLEN;
}

int l_ipv4_address_size()
{
    return sizeof(struct sockaddr_in);
}

int l_ipv6_address_size()
{
    return sizeof(struct sockaddr_in6);
}

int l_ipv4_address_align()
{
    return _Alignof(struct sockaddr_in);
}

int l_ipv6_address_align()
{
    return _Alignof(struct sockaddr_in6);
}

int l_epoll_create()
{
    int r = epoll_create(1);
    if (unlikely(r == -1))
    {
        return -errno;
    }
    return r;
}

int l_epoll_ctl(int epfd, int op, int socket, struct epoll_event *ev)
{
    int r = epoll_ctl(epfd, op, socket, ev);
    if (unlikely(r == -1))
    {
        return -errno;
    }
    return r;
}

int l_epoll_wait(int epfd, struct epoll_event *events, int maxEvents, int timeout)
{
    int r = epoll_wait(epfd, events, maxEvents, timeout);
    if (unlikely(r == -1))
    {
        return -errno;
    }
    return r;
}

int l_get_ipv4_address(struct sockaddr_in *sockAddr, char *addrStr, socklen_t len)
{
    const char *result = inet_ntop(AF_INET, &(sockAddr->sin_addr), addrStr, len);
    if (unlikely(result == NULL))
    {
        return -errno;
    }
    return 0;
}

int l_get_ipv6_address(struct sockaddr_in6 *sockAddr, char *addrStr, socklen_t len)
{
    const char *result = inet_ntop(AF_INET6, &(sockAddr->sin6_addr), addrStr, len);
    if (unlikely(result == NULL))
    {
        return -errno;
    }
    return 0;
}

uint16_t l_ipv4_port(struct sockaddr_in *sockAddr)
{
    return ntohs(sockAddr->sin_port);
}

uint16_t l_ipv6_port(struct sockaddr_in6 *sockAddr)
{
    return ntohs(sockAddr->sin6_port);
}

int l_ipv4_socket_create()
{
    int r = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);
    if (unlikely(r == -1))
    {
        return -errno;
    }
    return r;
}

int l_ipv6_socket_create()
{
    int r = socket(AF_INET6, SOCK_STREAM, IPPROTO_TCP);
    if (unlikely(r == -1))
    {
        return -errno;
    }
    return r;
}

int l_set_ipv4_sock_addr(struct sockaddr_in *sockAddr, char *address, uint16_t port)
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

int l_set_ipv6_sock_addr(struct sockaddr_in6 *sockAddr, char *address, uint16_t port)
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

int l_set_reuse_addr(int socket, int value)
{
    int r = setsockopt(socket, SOL_SOCKET, SO_REUSEADDR, (void *)&value, sizeof(value));
    if (unlikely(r == -1))
    {
        return -errno;
    }
    return r;
}

int l_set_keep_alive(int socket, int value)
{
    int r = setsockopt(socket, SOL_SOCKET, SO_KEEPALIVE, (void *)&value, sizeof(value));
    if (unlikely(r == -1))
    {
        return -errno;
    }
    return r;
}

int l_set_tcp_no_delay(int socket, int value)
{
    int r = setsockopt(socket, IPPROTO_TCP, TCP_NODELAY, (void *)&value, sizeof(value));
    if (unlikely(r == -1))
    {
        return -errno;
    }
    return r;
}

int l_set_ipv6_only(int socket, int value)
{
    int r = setsockopt(socket, IPPROTO_IPV6, IPV6_V6ONLY, (void *)&value, sizeof(value));
    if (unlikely(r == -1))
    {
        return -errno;
    }
    return r;
}

int l_get_err_opt(int socket, int *ptr)
{
    socklen_t ptr_size = sizeof(int);
    int r = getsockopt(socket, SOL_SOCKET, SO_ERROR, (void *)ptr, &ptr_size);
    if (unlikely(r == -1))
    {
        return -errno;
    }
    return r;
}

int l_set_nonblocking(int socket)
{
    int flag = fcntl(socket, F_GETFD, 0);
    int r = fcntl(socket, F_SETFL, flag | O_NONBLOCK);
    if (unlikely(r == -1))
    {
        return -errno;
    }
    return r;
}

int l_bind(int socket, void *sockAddr, socklen_t size)
{
    int r = bind(socket, (struct sockaddr *)sockAddr, size);
    if (unlikely(r == -1))
    {
        return -errno;
    }
    return r;
}

int l_listen(int socket, int backlog)
{
    int r = listen(socket, backlog < SOMAXCONN ? backlog : SOMAXCONN);
    if (unlikely(r == -1))
    {
        return -errno;
    }
    return r;
}

int l_connect(int socket, void *sockAddr, socklen_t size)
{
    int r = connect(socket, (struct sockaddr *)sockAddr, size);
    if (likely(r == -1))
    {
        return -errno;
    }
    return r;
}

int l_accept(int socket, void *clientAddr, socklen_t clientAddrSize)
{
    int r = accept(socket, (struct sockaddr *)clientAddr, &clientAddrSize);
    if (unlikely(r == -1))
    {
        return -errno;
    }
    return r;
}

ssize_t l_recv(int socket, void *buf, size_t len)
{
    int r = recv(socket, buf, len, 0);
    if (unlikely(r == -1))
    {
        return -errno;
    }
    return r;
}

ssize_t l_send(int socket, void *buf, size_t len)
{
    int r = send(socket, buf, len, 0);
    if (unlikely(r == -1))
    {
        return -errno;
    }
    return r;
}

int l_shutdown_write(int fd)
{
    int r = shutdown(fd, SHUT_WR);
    if (unlikely(r == -1))
    {
        return -errno;
    }
    return r;
}

int l_close(int fd)
{
    int r = close(fd);
    if (unlikely(r == -1))
    {
        return -errno;
    }
    return r;
}
