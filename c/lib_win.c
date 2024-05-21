#include "lib_win.h"
#include "share.h"
#include "wepoll.h"
#include <WS2tcpip.h>
#include <WinSock2.h>
#include <errno.h>
#include <stdio.h>

#pragma comment(lib, "ws2_32.lib")

int w_connect_block_code() { return WSAEWOULDBLOCK; }

int w_send_block_code() { return WSAEWOULDBLOCK; }

int w_interrupt_code() { return WSAEINTR; }

int w_ipv4_address_len() { return INET_ADDRSTRLEN; }

int w_ipv6_address_len() { return INET6_ADDRSTRLEN; }

int w_ipv4_address_size() { return sizeof(struct sockaddr_in); }

int w_ipv6_address_size() { return sizeof(struct sockaddr_in6); }

int w_ipv4_address_align() { return _Alignof(struct sockaddr_in); }

int w_ipv6_address_align() { return _Alignof(struct sockaddr_in6); }

int w_epoll_create(void **ptr)
{
    void *handle = epoll_create(1);
    if (unlikely(handle == NULL))
    {
        return -errno;
    }
    *ptr = handle;
    return 0;
}

int w_epoll_ctl(void *handle, int op, SOCKET socket,
                struct epoll_event *event)
{
    int r = epoll_ctl(handle, op, socket, event);
    if (unlikely(r == -1))
    {
        return -errno;
    }
    return r;
}

int w_epoll_wait(void *handle, struct epoll_event *events, int maxevents,
                 int timeout)
{
    int r = epoll_wait(handle, events, maxevents, timeout);
    if (unlikely(r == -1))
    {
        return -errno;
    }
    return r;
}

int w_epoll_close(void *handle)
{
    int r = epoll_close(handle);
    if (unlikely(r == -1))
    {
        return -errno;
    }
    return r;
}

int w_get_ipv4_address(struct sockaddr_in *clientAddr, char *addrStr, int len)
{
    const char *result =
        inet_ntop(AF_INET, &(clientAddr->sin_addr), addrStr, len);
    if (unlikely(result == NULL))
    {
        return -errno;
    }
    return 0;
}

int w_get_ipv6_address(struct sockaddr_in6 *clientAddr, char *addrStr,
                       int len)
{
    const char *result =
        inet_ntop(AF_INET6, &(clientAddr->sin6_addr), addrStr, len);
    if (unlikely(result == NULL))
    {
        return -errno;
    }
    return 0;
}

u_short w_ipv4_port(struct sockaddr_in *clientAddr)
{
    return ntohs(clientAddr->sin_port);
}

u_short w_ipv6_port(struct sockaddr_in6 *clientAddr)
{
    return ntohs(clientAddr->sin6_port);
}

int w_ipv4_socket_create(SOCKET *ptr)
{
    SOCKET r = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);
    if (unlikely(r == INVALID_SOCKET))
    {
        return -WSAGetLastError();
    }
    *ptr = r;
    return 0;
}

int w_ipv6_socket_create(SOCKET *ptr)
{
    SOCKET r = socket(AF_INET6, SOCK_STREAM, IPPROTO_TCP);
    if (unlikely(r == INVALID_SOCKET))
    {
        return -WSAGetLastError();
    }
    *ptr = r;
    return 0;
}

int w_set_ipv4_sock_addr(struct sockaddr_in *sockAddr, char *address,
                         u_short port)
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
            return -WSAGetLastError();
        }
        return r;
    }
}

int w_set_ipv6_sock_addr(struct sockaddr_in6 *sockAddr, char *address,
                         u_short port)
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
            return -WSAGetLastError();
        }
        return r;
    }
}

int w_set_reuse_addr(SOCKET socket, int value)
{
    int r = setsockopt(socket, SOL_SOCKET, SO_REUSEADDR, (char *)&value,
                       sizeof(value));
    if (unlikely(r == SOCKET_ERROR))
    {
        return -WSAGetLastError();
    }
    return r;
}

int w_set_keep_alive(SOCKET socket, int value)
{
    int r = setsockopt(socket, SOL_SOCKET, SO_KEEPALIVE, (char *)&value,
                       sizeof(value));
    if (unlikely(r == SOCKET_ERROR))
    {
        return -WSAGetLastError();
    }
    return r;
}

int w_set_tcp_no_delay(SOCKET socket, int value)
{
    int r = setsockopt(socket, IPPROTO_TCP, TCP_NODELAY, (char *)&value,
                       sizeof(value));
    if (unlikely(r == SOCKET_ERROR))
    {
        return -WSAGetLastError();
    }
    return r;
}

int w_set_ipv6_only(SOCKET socket, int value)
{
    int r = setsockopt(socket, IPPROTO_IPV6, IPV6_V6ONLY, (char *)&value,
                       sizeof(value));
    if (unlikely(r == SOCKET_ERROR))
    {
        return -WSAGetLastError();
    }
    return r;
}

int w_get_err_opt(SOCKET socket, int *ptr)
{
    int ptr_size = sizeof(int);
    int r = getsockopt(socket, SOL_SOCKET, SO_ERROR, (char *)ptr, &ptr_size);
    if (unlikely(r == SOCKET_ERROR))
    {
        return -WSAGetLastError();
    }
    return r;
}

int w_set_nonblocking(SOCKET socket)
{
    u_long argp = 1;
    int r = ioctlsocket(socket, FIONBIO, &argp);
    if (unlikely(r == SOCKET_ERROR))
    {
        return -WSAGetLastError();
    }
    return r;
}

int w_bind(SOCKET socket, void *sockAddr, int size)
{
    int r = bind(socket, (SOCKADDR *)sockAddr, size);
    if (unlikely(r == SOCKET_ERROR))
    {
        return -WSAGetLastError();
    }
    return r;
}

int w_listen(SOCKET socket, int backlog)
{
    int r = listen(socket, SOMAXCONN_HINT(backlog));
    if (unlikely(r == SOCKET_ERROR))
    {
        return -WSAGetLastError();
    }
    return r;
}

int w_connect(SOCKET socket, void *sockAddr, int size)
{
    int r = connect(socket, (SOCKADDR *)sockAddr, size);
    if (likely(r == SOCKET_ERROR))
    {
        return -WSAGetLastError();
    }
    return r;
}

int w_accept(SOCKET socket, SOCKET *ptr, void *clientAddr, int clientAddrSize)
{
    SOCKET r = accept(socket, (struct sockaddr *)clientAddr, &clientAddrSize);
    if (unlikely(r == INVALID_SOCKET))
    {
        return -WSAGetLastError();
    }
    *ptr = r;
    return 0;
}

int w_recv(SOCKET socket, char *buf, int len)
{
    int r = recv(socket, buf, len, 0);
    if (unlikely(r == SOCKET_ERROR))
    {
        return -WSAGetLastError();
    }
    return r;
}

int w_send(SOCKET socket, char *buf, int len)
{
    int r = send(socket, buf, len, 0);
    if (unlikely(r == SOCKET_ERROR))
    {
        return -WSAGetLastError();
    }
    return r;
}

int w_close_socket(SOCKET socket)
{
    int r = closesocket(socket);
    if (unlikely(r == SOCKET_ERROR))
    {
        return -WSAGetLastError();
    }
    return r;
}

int w_shutdown_write(SOCKET socket)
{
    int r = shutdown(socket, SD_SEND);
    if (unlikely(r == SOCKET_ERROR))
    {
        return -WSAGetLastError();
    }
    return r;
}

int w_clean_up()
{
    int r = WSACleanup();
    if (unlikely(r == SOCKET_ERROR))
    {
        return -WSAGetLastError();
    }
    return r;
}
