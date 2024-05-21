#ifndef TENET_WIN_H
#define TENET_WIN_H

#include "share.h"
#include "wepoll.h"
#include <WS2tcpip.h>
#include <WinSock2.h>

EXPORT_SYMBOL int w_connect_block_code();

EXPORT_SYMBOL int w_send_block_code();

EXPORT_SYMBOL int w_interrupt_code();

EXPORT_SYMBOL int w_ipv4_address_len();

EXPORT_SYMBOL int w_ipv6_address_len();

EXPORT_SYMBOL int w_ipv4_address_size();

EXPORT_SYMBOL int w_ipv6_address_size();

EXPORT_SYMBOL int w_ipv4_address_align();

EXPORT_SYMBOL int w_ipv6_address_align();

EXPORT_SYMBOL int w_epoll_create(void **ptr);

EXPORT_SYMBOL int w_epoll_ctl(void *handle, int op, SOCKET socket,
                              struct epoll_event *event);

EXPORT_SYMBOL int w_epoll_wait(void *handle, struct epoll_event *events,
                               int maxevents, int timeout);

EXPORT_SYMBOL int w_epoll_close(void *handle);

EXPORT_SYMBOL int w_get_ipv4_address(struct sockaddr_in *clientAddr,
                                     char *addrStr, int len);

EXPORT_SYMBOL int w_get_ipv6_address(struct sockaddr_in6 *clientAddr,
                                     char *addrStr, int len);

EXPORT_SYMBOL u_short w_ipv4_port(struct sockaddr_in *clientAddr);

EXPORT_SYMBOL u_short w_ipv6_port(struct sockaddr_in6 *clientAddr);

EXPORT_SYMBOL int w_ipv4_socket_create(SOCKET *ptr);

EXPORT_SYMBOL int w_ipv6_socket_create(SOCKET *ptr);

EXPORT_SYMBOL int w_set_ipv4_sock_addr(struct sockaddr_in *sockAddr,
                                       char *address, u_short port);

EXPORT_SYMBOL int w_set_ipv6_sock_addr(struct sockaddr_in6 *sockAddr,
                                       char *address, u_short port);

EXPORT_SYMBOL int w_set_reuse_addr(SOCKET socket, int value);

EXPORT_SYMBOL int w_set_keep_alive(SOCKET socket, int value);

EXPORT_SYMBOL int w_set_tcp_no_delay(SOCKET socket, int value);

EXPORT_SYMBOL int w_set_ipv6_only(SOCKET socket, int value);

EXPORT_SYMBOL int w_get_err_opt(SOCKET socket, int *ptr);

EXPORT_SYMBOL int w_set_nonblocking(SOCKET socket);

EXPORT_SYMBOL int w_bind(SOCKET socket, void *sockAddr, int size);

EXPORT_SYMBOL int w_listen(SOCKET socket, int backlog);

EXPORT_SYMBOL int w_connect(SOCKET socket, void *sockAddr, int size);

EXPORT_SYMBOL int w_accept(SOCKET socket, SOCKET *ptr, void *clientAddr,
                           int clientAddrSize);

EXPORT_SYMBOL int w_recv(SOCKET socket, char *buf, int len);

EXPORT_SYMBOL int w_send(SOCKET socket, char *buf, int len);

EXPORT_SYMBOL int w_close_socket(SOCKET socket);

EXPORT_SYMBOL int w_shutdown_write(SOCKET socket);

EXPORT_SYMBOL int w_clean_up();

#endif