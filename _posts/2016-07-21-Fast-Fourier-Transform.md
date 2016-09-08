---
layout: post
title: "旧识—快速傅里叶变换"
date: 2016-07-21 08:43:00
description: 'A primary tutorial for FFT.'
tags:
- math
- FFT&NTT
categories:
- Algorithms
---

# 扯扯扯

FFT是之前学过的...
然而比较尴尬的是之前觉得这东西学过敲过就不会忘,结果现在发现才过了几天有些细节就已经搞不清了...
~~难道是要提前步入老年的节奏~~

所以总之还是来补一点学习笔记吧!!

注:本篇博客偏向于应用,关于FFT的证明之类的就略过了(算导上的《多项式与快速傅里叶变换》中讲得非常详尽),~~(总之就是偷懒)~~所以思维比较跳跃,不建议初学者食用.

# 学学学

首先在OI界,我们可以简单地认为快速傅里叶变换就是用来加速两个多项式相乘的,可以把复杂度从$O(n^2)$降为$O(n\lg n)$,当然大整数相乘也可以看做是多项式相乘.

先搞清楚一点,我们可以通过把一个多项式的高次系数设为$0$来提高它的最高项次数.

然后两个$n-1$次多项式$A(x)=\sum_{k=0}^{n-1}a_kx^k,B(x)=\sum_{k=0}^{n-1}b_kx^k$相乘得到的$2n-1$次多项式$C(x)=\sum_{k=0}^{2n-1}c_kx^k$的系数是这样子的:

$$
c_k=\sum_{i=0}^ka_i\cdot b_{k-i}.
$$

这个式子看似简单,但是会敲(zhua)模板之后我们通常要做的就是构造多项式求解了,所以这个式子还是很重要的.
以及从这里可以看出来我们统一把下标从$0$开始会比较方便.

## 需要稍微知道一下的预备知识:

- **主$n$次单位根**:简称单位根,即$\omega_n=e^\frac{2\pi i}n$.方程$x^n=1$在复数域上的$n$个解为$x=e^\frac{2k\pi i}n=\omega_n^k,k\in[0,n).$
- **离散傅里叶变换**:已知$a_k,k\in[0,n)$,求$A(\omega_n^j)=\sum_{k=0}^{n-1}a_k(\omega_n^j)^k,j\in[0,n)$的过程叫做离散傅里叶变换(DFT),复杂度是$O(n\lg n)$.
- 由**范德蒙德矩阵**可以得到:$c_j=\frac 1{2n}\sum_{k=0}^{2n-1}C(\omega_{2n}^k)\omega_{2n}^{-jk}$,这个东西也可以用DFT来做.

## 然后说一下FFT的算法流程:

1. 对$A(x),B(x)$分别做DFT来求出$A(\omega_{2n}^k)$以及$B(\omega_{2n}^k)$,$k\in[0,2n)$,复杂度是$O(n\lg n)$.
2. 花$O(n)$的时间复杂度计算出$C(\omega_{2n}^k)=A(\omega_{2n}^k)\cdot B(\omega_{2n}^k),k\in[0,2n)$.
3. 再用DFT花$O(n\lg n)$的时间复杂度算出$c_k$.

这里要注意,在步骤$1$时必须要把$A(x),B(x)$都补成$n-1$次的多项式,其中$n$为$2$的幂.为了防止搞错,最好都使用左闭右开的方式表示区间.

## 然后讲DFT的流程,这里直接讲的是**迭代**实现.
在此之前有一点要特别注意,下面的$n$指的并不是原来的多项式$A(x),B(x)$是$n-1$次,而是得到的$C(x)$为$n-1$次,即这里的$n$应该是原来的$n$的两倍,千万不能搞混了.

首先我们需要把数组按照下标$i$的**位逆序置换**$rev[i]$从小到大排序.
$rev[i]$即是将$i$的$\lg n$位二进制表示左右翻转,可以在$O(n)$时间内预处理出来,然后这个排序也是可以机智地做到$O(n)$的,具体实现看代码.

然后我们可以画出一棵递归树并自底向上合并,个人感觉这里只要稍微注意点并不会错.

# 用用用

实际应用时可以用STL中的complex类,当然手写复数会快很多,而且也不麻烦.
由于涉及复数计算,精度肯定也是一个问题,但是在一般情况下似乎计算结果还是比较准确的.

容易错的还是之前提到过的要将多项式的次数提高,因此数组要开大一些,还有DFT时的$n$不能和输入中的$n$弄混.

## 贴一份改过的FFT模板:

```c++
#include <cstdio>
#include <cmath>
#include <complex>
#include <algorithm>
using namespace std;
const int N=(1<<18)+5;
const double pi=acos(-1.0);
typedef complex<double> Com;
int rev[N];
Com A[N],B[N],C[N];
template<class T>void rd(T &res){
    res=0;
    char c;
    while(c=getchar(),c<48);
    do res=res*10+(c^48);
        while(c=getchar(),c>47);
}
void DFT(Com *arr,int n,int sgn){
    for(int i=0;i<n;++i)
        if(i<rev[i])
            swap(arr[i],arr[rev[i]]);
    for(int m=2;m<=n;m<<=1){
        Com wm(cos(2*pi/m),sgn*sin(2*pi/m));
        for(int i=0;i<n;i+=m){
            Com w(1,0);
            for(int j=0;j<m>>1;++j,w*=wm){
                Com x=arr[i+j],y=w*arr[i+j+(m>>1)];
                arr[i+j]=x+y;
                arr[i+j+(m>>1)]=x-y;
            }
        }
    }
}
void FFT(int n,int m){
    int _n,S;
    for(_n=1,S=0;_n<n+m;_n<<=1,++S);
    for(int i=1;i<_n;++i)
        rev[i]=(rev[i>>1]>>1)|((i&1)<<S-1);
    for(int i=n;i<_n;++i)
        A[i]=0;
    for(int i=m;i<_n;++i)
        B[i]=0;
    DFT(A,_n,1);
    DFT(B,_n,1);
    for(int i=0;i<_n;++i)
        C[i]=A[i]*B[i];
    DFT(C,_n,-1);
    for(int i=0;i<n+m;++i)
        C[i]/=_n;
}
int main(){
    int n,m,S;
    rd(n);rd(m);
    ++n;++m;
    for(int i=0,tmp;i<n;++i){
        rd(tmp);
        A[i]=tmp;
    }
    for(int i=0,tmp;i<m;++i){
        rd(tmp);
        B[i]=tmp;
    }
    FFT(n,m);
    for(int i=0;i<=n+m-2;++i)
        printf("%d%c",(int)(C[i].real()+0.5)," \n"[i==n+m-2]);
    return 0;
}
/*
    
    Jul.21.16

    Problem:UOJ #34
    
*/

```

## 练练练

裸题就是[UOJ#34](http://uoj.ac/problem/34).

构造多项式求解的题有[BZOJ3527](http://kyleyoung-ymj.cf/BZOJ-3527)和[HDU5307](http://kyleyoung-ymj.cf/HDU-5307).

FFT还有一种用法是配合分治来优化dp,题目有[HDU5730](http://acm.hdu.edu.cn/showproblem.php?pid=5730)以及[CF553E](http://codeforces.com/problemset/problem/553/E).