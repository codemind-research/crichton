#include <stdio.h>

int* numPtr;
int* numPtr2;

int main()
{
    for(int i = 0; i < 100; i++) {
        if(numPtr[i] == numPtr2[i]){
            return 1;
        }
    }
    return 0;
}