#include <stdio.h>

int main() {
  int i = 0; 
  while(1) {
    if(i < 10 ) {
      printf("in the if block\n");
      i++;
      continue;
    }
    else if( i < 15 ){
      printf("in the else if block\n");
      i++;
      continue;
    }

    break;

  }

  return 0;
}
