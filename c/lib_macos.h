#ifndef LIB_MACOS_H
#define LIB_MACOS_H

#include <sys/socket.h>
#include <sys/event.h>
#include <netinet/in.h>

EXPORT_SYMBOL int m_connect_block_code();

EXPORT_SYMBOL int m_send_block_code();

EXPORT_SYMBOL int m_interrupt_code();

EXPORT_SYMBOL int m_ipv4_address_len();

EXPORT_SYMBOL int m_ipv6_address_len();

EXPORT_SYMBOL int m_ipv4_address_size();

EXPORT_SYMBOL int m_ipv6_address_size();

EXPORT_SYMBOL int m_ipv4_address_align();

EXPORT_SYMBOL int m_ipv6_address_align();

EXPORT_SYMBOL int m_kqueue();

EXPORT_SYMBOL int m_kevent_ctl(int kq, struct kevent *changelist, int nchanges);

EXPORT_SYMBOL int m_kevent_wait(int kq, struct kevent *eventlist, int nevents, int timeout);

EXPORT_SYMBOL int m_get_ipv4_address(struct sockaddr_in *sockAddr, char *addrStr, socklen_t len);

EXPORT_SYMBOL int m_get_ipv6_address(struct sockaddr_in6 *sockAddr, char *addrStr, socklen_t len);

EXPORT_SYMBOL uint16_t m_ipv4_port(struct sockaddr_in *sockAddr);

EXPORT_SYMBOL uint16_t m_ipv6_port(struct sockaddr_in6 *sockAddr);

EXPORT_SYMBOL int m_ipv4_socket_create();

EXPORT_SYMBOL int m_ipv6_socket_create();

EXPORT_SYMBOL int m_set_ipv4_sock_addr(struct sockaddr_in *sockAddr, char *address, uint16_t port);

EXPORT_SYMBOL int m_set_ipv6_sock_addr(struct sockaddr_in6 *sockAddr, char *address, uint16_t port);

EXPORT_SYMBOL int m_set_reuse_addr(int socket, int value);

EXPORT_SYMBOL int m_set_keep_alive(int socket, int value);

EXPORT_SYMBOL int m_set_tcp_no_delay(int socket, int value);

EXPORT_SYMBOL int m_set_ipv6_only(int socket, int value);

EXPORT_SYMBOL int m_get_err_opt(int socket, int *ptr);

EXPORT_SYMBOL int m_set_nonblocking(int socket);

EXPORT_SYMBOL int m_bind(int socket, void *sockAddr, int size);

EXPORT_SYMBOL int m_listen(int socket, int backlog);

EXPORT_SYMBOL int m_connect(int socket, void *sockAddr, int size);

EXPORT_SYMBOL int m_accept(int socket, void *clientAddr, socklen_t clientAddrSize);

EXPORT_SYMBOL ssize_t m_recv(int socket, void *buf, size_t len);

EXPORT_SYMBOL ssize_t m_send(int socket, void *buf, size_t len);

EXPORT_SYMBOL int m_shutdown_write(int fd);

EXPORT_SYMBOL int m_close(int fd);

#endif