#include "library.h"

extern "C" {

struct S {
    char* name;
};

struct S s;

union U {
    float f;
    double d;
};

union U u;

char array[5];

}