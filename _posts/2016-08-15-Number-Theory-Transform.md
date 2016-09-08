---
layout: post
title: "新知—快速数论变换"
date: 2016-08-15 22:34:00
description: 'A primary tutorial for NTT.'
tags:
- math
- number theory
- FFT&NTT
categories:
- Algorithms
---

# 扯扯扯

之前学FFT的时候就知道了有NTT这么个东西...
但是因为暂时没碰到这样的题目,还有数论知识不太够就坑掉了...
现在滚回来学...

# 学学学

众所周知,FFT由于涉及复数计算,计算速度和精度都是问题.

所以在为了保证答案准确或者题目要求结果对大素数取模的情况下,我们可以选择使用快速数论变换.

先补一些数论知识:

## 数论阶

若$a\perp p$,则称满足$a^r\equiv1\pmod p$的最小的$r$为$a$模$p$的阶,记为$\delta_p(a)$.

由欧拉定理可知,$\delta_p(a)\le\varphi(p)$.

## 原根

若$\delta_p(r)=\varphi(p)$,则称$r$是模数$p$的一个原根.

注意不是对于所有$p$都存在原根,这个$p$需要满足一定条件,一般题目给出的模数都会满足这个条件,等一下再讲.

原根的求法,似乎只能从小到大暴力枚举$r$来求出一个最小的$r$,当然,我们不能直接暴力根据定义判定$r$是否为原根,需要用到下面这个判定定理:

令$p-1=\prod_ip_i^{\alpha_i}$,其中$p_i$是$p-1$的不同素因子.
若对所有的$i$,都满足$r^{\frac{\varphi(p)}{p_i}}\not\equiv1\pmod p$,则$r$为$p$的一个原根.

一般所求的原根不会很大,可以直接用这个判定方法暴力求解.

题目给出的模数,一般都会是这样形式的一个大素数: $p=a\cdot2^m+1$,其中$a$是个不大的数,并且包含的不同素因子个数比较少(一般会给出其素因子分解).

最常见的就是这个数:$998244353(=7\times17\times2^{23}+1)$,是一个素数,其原根为$3$.

## NTT

然后知道了原根有和单位根类似的性质...~~(根本不知道为什么)~~

我们可以完全用$r^{\frac{p-1}m}$来代替$\omega_m=e^{\frac{2\pi i}m}$,然后所有计算都变成模$p$意义下的计算了.

其他的种种都和FFT一样了...

# 用用用

贴一份NTT实现的多项式乘法([UOJ#34](http://uoj.ac/problem/34)).

```c++
#include <cmath>
#include <ctime>
#include <cctype>
#include <cstdio>
#include <cstring>
#include <cstdlib>
#include <cassert>
#include <set>
#include <map>
#include <stack>
#include <queue>
#include <vector>
#include <bitset>
#include <complex>
#include <iostream>
#include <algorithm>
#define fi first
#define se second
#define y1 jfskav
#define pb push_back
#define lson (k<<1)
#define rson (k<<1|1)
#define lowbit(x) (x&-x)
#define debug(x) cout<<#x<<"="<<x<<endl
#define rep(i,s,t) for(register int i=s,_t=t;i<_t;++i)
#define per(i,s,t) for(register int i=t-1,_s=s;i>=_s;--i)
using namespace std;
typedef long long ll;
typedef unsigned long long ull;
typedef unsigned int uint;
typedef double db;
typedef pair<int,int> pii;
typedef pair<ll,ll> pll;
typedef vector<int> veci;
const int mod=(int)1e9+7,inf=0x7fffffff,rx[]={-1,0,1,0},ry[]={0,1,0,-1};
const ll INF=1ll<<60;
const db pi=acos(-1),eps=1e-6;
template<class T>void rd(T &x){
    x=0;
    char c;
    while(c=getchar(),c<48);
    do x=(x<<3)+(x<<1)+(c^48);
        while(c=getchar(),c>47);
}
template<class T>void rec_pt(T x){
    if(!x)return;
    rec_pt(x/10);
    putchar(x%10^48);
}
template<class T>void pt(T x){
    if(!x)putchar('0');
    else rec_pt(x);
}
template<class T>inline void ptn(T x){
    pt(x),putchar('\n');
}
template<class T>inline void Max(T &a,T b){
    if(b>a)a=b;
}
template<class T>inline void Min(T &a,T b){
    if(b<a)a=b;
}
template<class T>T gcd(T a,T b){
    return b?gcd(b,a%b):a;
}

const int P=(7*17<<23)+1,r=3;

inline void mod_add(int &a,int b){
    if((a+=b)>=P)a-=P;
}
inline void mod_minus(int &a,int b){
    if((a-=b)<0)a+=P;
}
int fast_mod_pow(int a,int b){
    int res=1;
    for(;b;b>>=1,a=1ll*a*a%P)
        if(b&1)res=1ll*res*a%P;
    return res;
}
inline int calc_inv(int x){
    return fast_mod_pow(x,P-2);
}


const int N=(1<<18)+5;

int rev[N],A[N],B[N],C[N];

void DFT(int *arr,int n,bool flag){
    rep(i,0,n)if(i<rev[i])swap(arr[i],arr[rev[i]]);
    for(int m=2;m<=n;m<<=1){
        int g=fast_mod_pow(r,(P-1)/m);
        if(flag)g=calc_inv(g);
        for(int i=0;i<n;i+=m){
            int cur=1;
            rep(j,0,m>>1){
                int x=arr[i+j],y=1ll*cur*arr[i+j+(m>>1)]%P;
                mod_add(arr[i+j]=x,y);
                mod_minus(arr[i+j+(m>>1)]=x,y);
                cur=1ll*cur*g%P;
            }
        }
    }
}
void NTT(int n,int m){
    int _n,S;
    for(_n=1,S=0;_n<n+m;_n<<=1,++S);
    rep(i,1,_n)rev[i]=(rev[i>>1]>>1)|((i&1)<<S-1);
    rep(i,n,_n)A[i]=0;
    rep(i,m,_n)B[i]=0;
    DFT(A,_n,false);
    DFT(B,_n,false);
    rep(i,0,_n)C[i]=1ll*A[i]*B[i]%P;
    DFT(C,_n,true);
    int inv=calc_inv(_n);
    rep(i,0,_n)C[i]=1ll*C[i]*inv%P;
}

int main(){
    int n,m;
    rd(n),rd(m);
    ++n,++m;
    rep(i,0,n)rd(A[i]);
    rep(i,0,m)rd(B[i]);
    NTT(n,m);
    rep(i,0,n+m-1)pt(C[i]),putchar(" \n"[i==n+m-2]);
    return 0;
}
```