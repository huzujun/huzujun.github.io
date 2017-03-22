---
layout: post
title: "炫酷反演魔术 魔术揭秘"
date: 2016-08-15 16:24:00
description: 'A primary tutorial for Inversion.'
tags:
- math
- number theory
- inversion
- convolution
- inclusion-exclusion principle
categories:
- Algorithms
---

>刚才的标题是唬人的...

请允许我先丢个[链接](http://vfleaking.blog.uoj.ac/blog/87),然后开始扯淡.

# 开场

vfk老师在这份课件中深入浅出地讲解了各种OI中~~常用~~的反演,本蒟蒻表示并没有完全看懂(尤其是对于矩阵的一部分知识不是非常熟悉),而且由于网上关于反演的资料除了莫比乌斯反演之外非常少,所以本蒟蒻在这里只是谈谈个人对反演的理解,如果出现了错误还请各路神犇**不吝赐教**.

还有就是课件中的推导已经写得非常漂亮了,我也没必要在这里再自己敲一遍(所以写这篇博客的目的何在?),有些地方还是直接"抛结论跑"好了.

# 揭秘

**反演**的思路是用未知量表示已知量,然后反过来推出未知量的表达式.下面我们默认$f$表示已知量,$g$表示未知量.

课件中介绍了**二项式反演**,**莫比乌斯反演**,**子集反演**等等,这些反演都与**容斥原理**有着密不可分的关系,就是说都可以用容斥来偏"意识流"地理解.但是不管怎么说,用反演来推导显得比较有说(zhuang)服(bi)力(fan).

推导反演的过程,就是"找到了一个if语句,说了一句废话,代进去搞搞居然就凑出来了个$f$".当然这其中需要很多技巧,可以通过看课件中的推导来体会.

## 二项式反演

$$
\begin{eqnarray}
f(n)&=&\sum_{k=0}^n\binom nkg(k),\\
g(n)&=&\sum_{k=0}^n(-1)^{n-k}\binom nkf(k).
\end{eqnarray}
$$

推导的if语句是$\sum_{k=0}^n(-1)^k\binom nk=[n=0]$.
说的废话是$g(n)=\sum_{m=0}^n[n-m=0]\binom nmg(m)$.

## 莫比乌斯反演

一个方向:

$$
\begin{eqnarray}
f(n)&=&\sum_{d\mid n}g(d),\\
g(n)&=&\sum_{d\mid n}\mu(\frac nd)f(d).
\end{eqnarray}
$$

另一个方向:

$$
\begin{eqnarray}
f(n)&=&\sum_{n\mid d}g(d),\\
g(n)&=&\sum_{n\mid d}\mu(\frac dn)f(d).
\end{eqnarray}
$$

说实话我并不知道反方向的是怎么推的,但是结论非常好记.

if语句:$\sum_{d\mid n}\mu(d)=[n=1]$.
废话:$g(n)=\sum_{m\mid n}[\frac nm=1]g(m)$.

## 子集反演

一个方向:

$$
\begin{eqnarray}
f(S)&=&\sum_{T\subseteq S}g(T),\\
g(S)&=&\sum_{T\subseteq S}(-1)^{\mid S\mid-\mid T\mid}f(T).
\end{eqnarray}
$$

另一个方向:

$$
\begin{eqnarray}
f(S)&=&\sum_{S\subseteq T}g(T),\\
g(S)&=&\sum_{S\subseteq T}(-1)^{\mid T\mid-\mid S\mid}f(T).
\end{eqnarray}
$$

还是只会推正方向的...

if语句:$\sum_{r\subseteq p}(-1)^{\mid r\mid}=[p=0]$.
废话:$g(p)=\sum_{r\subseteq p}[p-r=0]g(r)$.

~~话说从这里开始变量名就有点奇怪了呢...~~

这个推导和二项式反演的推导非常相似.

实际运用时,这个东西可以用高维前缀和的写法$O(n\cdot2^n)$来做,具体看下面的"$or$卷积"部分.

## 离散傅里叶变换

$$
\begin{eqnarray}
f(m)&=&\sum_{k=0}^{n-1}\omega_n^{mk}g(k),\\
g(m)&=&\frac 1n\sum_{k=0}^{n-1}\omega_n^{-mk}f(k).
\end{eqnarray}
$$

这个东西实质上也是反演...但是我并不会用反演推...

给的if语句是$\frac 1n\sum_{k=0}^{n-1}\omega_n^{mk}=[m\bmod n=0]$.
然而我并不知道该说什么废话...

不过课件里那个"又一道经典题"是可以用这个if语句推的.

## or卷积

给定$a_0,\dots,a_{2^n-1},b_0,\dots,b_{2^n-1}$,求:

$$
c_r=\sum_{p,q}[p\cup q=r]a_pb_q,r\in[0,2^n).
$$

关于这个名称应该没有正规的定义吧...似乎这个和下面的那个卷积都应该是"子集卷积"?还是看个人理解吧,我比较偏向于专门把下面的那个卷积称为"子集卷积".

不管怎样,这个$or$卷积是子集反演的一个经典应用.

令$c_r'=\sum_{p\subseteq r}c_p$,类似地定义$a_r',b_r'$,可以推导出$c_r'=a_r'b_r'$,求出$c_r'$后用子集反演求出$c_r$.

具体实现时,直接略无脑地用高维前缀和正着倒着搞就可以实现子集反演了,复杂度是$O(n\cdot2^n)$.

另外,$and$卷积可以直接先把集合取补集转化成$or$卷积来做.

### Code

```c++
void or_conv(){
    rep(i,0,n)rep(j,1,1<<n)if(j&1<<i){
        A[j]+=A[j^1<<i];
        B[j]+=B[j^1<<i];
    }
    rep(i,0,1<<n)C[i]=A[i]*B[i];
    rep(i,0,n)rep(j,1,1<<n)if(j&1<<i)
        C[j]-=C[j^1<<i];
}
```

## 子集卷积

给定$a_0,\dots,a_{2^n-1},b_0,\dots,b_{2^n-1}$,求:

$$
c_r=\sum_{p\subseteq r}a_pb_{r-p},r\in[0,2^n).
$$

这个...似乎课件上的推导有些问题(当然很有可能是我没看懂),这里~~良心地~~写一下我的推法.

如果直接用交集和并集的限制是推不出来的,我们必须再加一个维度.

令$c_{i,j}=\sum_{p,q}[\mid p\mid+\mid q\mid=i][p\cup q=j]a_pb_q$,
还有$c_{i,j}'=\sum_{s\subseteq j}c_{i,s}$.

那么:

$$
\begin{eqnarray}
c_{i,j}'
&=&\sum_{s\subseteq j}c_{i,s}\\
&=&\sum_{s\subseteq j}\sum_{p,q}[\mid p\mid+\mid q\mid=i][p\cup q=s]a_pb_q\\
&=&\sum_{p,q}[\mid p\mid+\mid q\mid=i][p\cup q\subseteq j]a_pb_q\\
&=&\sum_{p,q}[\mid p\mid+\mid q\mid=i][p\subseteq j][q\subseteq j]a_pb_q\\
&=&\sum_{k=0}^i\sum_{p\subseteq j}[\mid p\mid=k]a_p\sum_{q\subseteq j}[\mid q\mid=i-k]b_q\\
&=&\sum_{k=0}^ia_{k,j}b_{i-k,j}.
\end{eqnarray}
$$

然后再用子集反演,可以推出:

$$
c_{i,j}=\sum_{s\subseteq j}(-1)^{\mid j\mid-\mid s\mid}\sum_{k=0}^ia_{k,j}b_{i-k,j}.
$$

最终答案就是$c_r=c_{\mid r\mid,r}$.

这样复杂度就可以做到$O(n^2\cdot2^n)$了.

但是直接暴力做,复杂度是$O(3^n)$,凭借极小的常数在$n=17$时仍可以碾压标程...

### Code

```c++
const int N=17,M=1<<N;

int n,bitcnt[M],A[M],B[M],C[M],sum_A[N+1][M],sum_B[N+1][M],sum_C[N+1][M];

inline void init(){
    rep(i,1,M)bitcnt[i]=bitcnt[i>>1]+(i&1);
}
void conv(){

    rep(i,0,n+1)rep(j,0,1<<n){
        if(i==bitcnt[j]){
            sum_A[i][j]=A[j];
            sum_B[i][j]=B[j];
        }
        else sum_A[i][j]=sum_B[i][j]=0;
    }

    rep(i,0,n)rep(j,1,1<<n)if(j&1<<i)rep(k,0,bitcnt[j]){
        mod_add(sum_A[k][j],sum_A[k][j^1<<i]);
        mod_add(sum_B[k][j],sum_B[k][j^1<<i]);
    }

    rep(i,0,n+1)rep(j,0,1<<n){
        sum_C[i][j]=0;
        rep(k,0,i+1)sum_C[i][j]=(sum_C[i][j]+1ll*sum_A[k][j]*sum_B[i-k][j])%mod;
    }

    rep(i,0,n)rep(j,1,1<<n)if(j&1<<i)rep(k,0,n+1)
        mod_minus(sum_C[k][j],sum_C[k][j^1<<i]);

    rep(i,0,1<<n)C[i]=sum_C[bitcnt[i]][i];

}
```

# 收场

>妈呀最怕这种时候要我总结点什么了！

大概我对反演的理解也就是这些了...

我觉得学习反演,不应该只记个结论,中间的推导过程也是很有价值的.

那就先这样吧.
