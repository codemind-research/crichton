#include <stdio.h>
#include <stdlib.h>
#include <assert.h>

int x = 0;
int y = 0;
int z = 0;
int w = 0;
void aaa(char * x, char y, int z) {

}

void atest1(int a) {
  if(y > 10)
    printf("%d %d",a, y);
  else
    assert(y != 10);
}

void atest1_2(int a) {
  if(y > 10)
    printf("%d %d",a, y);
  else
    y = a;
}

void atest1_3(int a) {
  if(y > 10)
    printf("%d %d",a, y);
  
  y = a;
}

void atest1_4(int a) {
  if(y > 10)
    printf("%d %d",a, y);
  
  assert(y != 10);
}
int btest(int a, int b){
  return -1;
}
int atest3(int a, int b) {
  if((x > 0 
    && b > 1 
    && a > 1)
    || a + b > 0) {
    return a + b + w;
  }
  
  else if(x > 0 
          && b > 1) {
    return a + b;
  }
  int test = btest(a,b);
  if (test > 0){
    return a + b;
  }
  return a * b;
}

int atest3_4(int a, int b) {
  if((x > 0 
    && b > 1 
    && a > 1)
    || a + b > 0)
      printf("true");
  else if(x > 0 && b > 1)
      printf("false");

  printf("oops");

  return a * b;
}

int atest3_4_1(int a, int b) {
  if(x==1)
      printf("true");
  else if(x==2)
      printf("false");

  printf("oops");

  return a * b;
}

int atest3_5(int a, int b) {
  int res;
  if((x > 0 
    && b > 1 
    && a > 1)
    || (res = atest3_4(a,b)))
      res = res?1:2;
  else if(x > 0 && b > 1)
      printf("false");

  printf("oops");

  return a * b;
}

int atest3_6(int a, int b) {
  if((a>1 || b>1) && x>1)
      printf("true");
  else
      printf("false");

  printf("oops");

  return a * b;
}

int atest5(int a, int b) {
  while(a<10)
    a++;   

  return a;
}

int atest7(int a, int b) {
  for(int i=0; i<b; i++)
    a++;   

  return a;
}

// TODO:
int atest8(int a, int b) {
  switch(a) {
    case 1:
    case 2:
    case 3:
      return b + 10;

    case 4:
      return b + 20;

    case 5:
    case 6:
      break;
  }

  return b;
}


int atest6_1(int a, int b) {
  if(a < -1 && b > 0 && x < -1){
    return 0;
  }
  if((a>1 || b>1) && x>1) {
    a++;   
  }

  return a;
}

int atest3_2(int a, int b) {
  if(x > 0) 
    if(b > 1)
      if(a > 1)
        return a + b + w;
  
  return a * b;
}
int vtest(char* test){
  
  if (test == "abc"){
    return 0;
  }
  else{
    return 1;
  }
}
int atest3_3(int a, int b) {
  int bbb = (x > 0 
    && b > 1 
    && a > 1)
    || a + b > 0;

  if(bbb) {
    return a + b + w;
  }
  else if(x > 0 
          && b > 1) {
    return a + b;
  }


  return a * b;
}

void atest2(float a, int b, double c) {
  printf("%f",a);
  printf("%d",b);
  printf("%lf",c);
  printf("%d",z);

  atest3(a, b);
}

void atest2_call() {
  atest2(1,2,3);
}

void atest4(int a, int b) {
  if(a + b > 10)
    aaa("%d %d",a, b);
  else
    aaa("%d %d",a, b);
}

int main() {
  return 0;
}
