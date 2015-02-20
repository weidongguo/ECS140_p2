#include <stdio.h>
main() {
  int x_i, x_k = -12345, x_long = -12345, x_n = -12345;
  x_i = 25;
  while( 10 != x_i ) {
    if( 20 == x_i ) {
      x_k = 20 - x_i;
      while( x_k ) {
        printf("%d\n", x_k);
    }
        x_k = x_k + 1;
      }

    }


    else if( 15 == x_i ) {
      x_k = 15 - x_i;
      x_n = 5;
      while( x_k ) {
      x_long = 10 * x_k;
      printf("%d\n", x_long);
      x_k = x_k + 1;
      }

      else if( x_n ) {
      printf("%d\n", x_n);
      x_n = 0;
      }

      printf("%d\n", x_long);
    }


    else {
      x_k = 10 - x_i;
      while( x_k ) {
      printf("%d\n", x_k);
      x_k = x_k + 1;
      }

    }

    x_i = x_i - 1;
  }

  printf("%d\n", x_i);
}
