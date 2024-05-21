TARGET :=
CLEAN_CMD :=
BUILD_CMD :=

ifeq ($(OS),Windows_NT)
    TARGET := tenet.dll
    CLEAN_CMD := del .\lib\$(TARGET)
    BUILD_CMD := clang -std=c11 -fcolor-diagnostics -fansi-escape-codes -Wall -Wextra -Werror -g0 -shared -O3 -o .\lib\$(TARGET) -v .\src\share.c .\src\rpmalloc.c .\src\lib_win.c .\src\wepoll.c -lAdvapi32
else
    UNAME_S := $(shell uname -s)
    ifeq ($(UNAME_S),Linux)
        TARGET := libtenet.so
        CLEAN_CMD := rm -rf ./lib/$(TARGET)
        BUILD_CMD := clang -std=c11 -fcolor-diagnostics -fansi-escape-codes -Wall -Wextra -Werror -g0 -shared -O3 -fPIC -flto -fvisibility=hidden -D_GNU_SOURCE -o ./lib/$(TARGET) -v ./c/share.c ./c/rpmalloc.c ./c/lib_linux.c
    endif
    ifeq ($(UNAME_S),Darwin)
        TARGET := libtenet.dylib
        CLEAN_CMD := rm -rf ./lib/$(TARGET)
        BUILD_CMD := clang -std=c11 -fcolor-diagnostics -fansi-escape-codes -Wall -Wextra -Werror -g0 -shared -O3 -fPIC -flto -fvisibility=hidden -o ./lib/$(TARGET) -v ./c/share.c ./c/rpmalloc.c ./c/lib_macos.c
    endif
endif

.PHONY: build clean
build:
	$(BUILD_CMD)
clean:
	$(CLEAN_CMD)