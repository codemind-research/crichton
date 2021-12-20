#include <iostream>
#include <limits>
#include <exception>

using namespace std;

struct Test {
  int x;
  double y;
  char xn;
};

class TTT {
public:
  int x;
  int y;
};

template<typename T>
class tempA {
public:
T tp_func(T x, Test n, const TTT **d) {
  return x + x;
}
T tp_func2(T x, const Test n, const TTT** const d);
};

template<typename T>
T tempA<T>::tp_func2(T x, const Test n, const TTT** const d) {
  return x + x;
  cout << x << x << endl;
}

void call_to_func(tempA<int> x) {
  Test n;
  TTT *a;
  // if (a == nullptr)
  //   x.tp_func(1, n, (const TTT**)&a);
  // else
  //   x.tp_func2(1, n, (const TTT**)&a);
}

class TTTT {
public:
virtual bool test(int y) {
  return y % 2;
}
};

int main(int argc, char *argv[]) {
  tempA<int> x;
  call_to_func(x);

  int y = 0;
  cin >> y;
  TTTT n;
  if (n.test(y))
    return 0;
  if (y == 0)
    cout << "zero" << endl;
  else if (y == 7)
    cout << "lucky" << endl;
  else if (y % 2 == 0)
    cout << "even" << endl;
  else 
    cout << y << endl;
}


// int getTTTTT();

// class AAA{
//   private:
//     virtual void TT3();
// };
// namespace TTTSD{
// class VirtualClass{
//   public:
//     VirtualClass();
//     virtual ~VirtualClass() noexcept;
//     virtual int getTTTTT() { return 1; }
// };
// };

// void AAA::TT3(){
//   TTTSD::VirtualClass vc;
//   if (vc.getTTTTT() < 0)
//     return;
//   try {

//   } catch (std::exception e) {

//   }
// }