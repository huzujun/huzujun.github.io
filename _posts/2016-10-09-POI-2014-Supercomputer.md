---
layout: post
title: "POI2014 Supercomputer"
date: 2016-10-09 20:58:00
description: 'Solution for POI2014 Supercomputer.'
tags:
- tree
- math
- convex shell
categories:
- POI
---

# Description

给定一棵$n$个节点的有根树,根节点为$1$.$m$次询问,每次给出$K$,按如下规则进行操作:

- 第一次操作必须选择根节点.

- 接下来每次操作可以选择最多$K$个节点,它们的父亲节点在之前必须已被选择.


求最少进行几次操作可以将所有节点全部选择.

$1\le n,m,K\le10^6.$

# Solution

有一个神奇的结论是这样:

> 记根节点的深度为$1$,那么一定存在$i$,满足前$i$次操作恰好能取完前$i$层的所有节点,并且之后的每次操作(除了最后一次)都能取到$K$个节点.
>

$\text{Proof.}$

记$\text{mx}=\max\limits_{u\in V}\lbrace \text{dep}_u\rbrace$,对于$1\le i\le\text{mx}$,考虑按如下决策选择节点:

- 第$i$次操作时考虑在第$i$层中取$K$个节点(特别地,第$1$次操作时只取一个节点).
- 若此时第$i$层可选的节点个数大于等于$K$,则在其中选取$K$个,并且满足所选节点的子树中深度最深的节点尽量深.
- 若此时第$i$层可选的节点不足$K$个,那么取完这些节点,并且在之前的层中取尽量多的节点.


那么这样操作$\text{mx}$次之后应该可以满足接下来的操作除了最后一次都可以取满$K$个节点.(如何证明?)

考虑满足前$i$次操作恰取完前$i$层的所有节点(记为性质$P$)的$i$,易知这样的$i$一定存在($i=1$就是一个满足条件的$i$).

如果对于这样的一个$i$,它之后的操作(除了最后一次)不能够都取到$K$个,不妨令之后第一次不能满足的是第$j$次操作.

那么$j\le\text{mx}$,于是由决策可知在第$j$次操作时取完了前$j$层的所有节点,所以$j$也满足性质$P$.

从而可以知道一定存在满足$P$的$i$,它之后的操作除了最后一次都能取到$K$个节点.

$\text{Q.E.D.}$

---

接下来考虑满足上证性质的$i$,由于前$i$次操作只能选择深度不超过$i$的节点,所以这种取法是最优的,可以得到总操作次数为$i+\lceil\frac{cnt_i}K\rceil$,其中$cnt_i$表示深度大于$i$的节点个数.如果有多个满足此性质的$i$,算出来的答案应该是一样的.

而对于其它不满足该性质的$i$,用上述公式计算出的操作次数可能会偏小,所以答案即为$\max\limits_{1\le i\le\text{mx}}\lbrace i+\lceil\frac{cnt_i}K\rceil\rbrace$.

化简一下,即求$\lceil\max\limits_{1\le i\le\text{mx}}\lbrace iK+cnt_i\rbrace/K\rceil$,$\max$里面那个东西是个关于$K$的一次函数,可以维护一个上凸壳,对于所有的$K$在凸壳上扫一遍即可求出答案了.

复杂度$O(n+\max\lbrace K\rbrace)$.

# Code

```c++
#include <bits/stdc++.h>
#define fi first
#define se second
#define y1 jfskav
#define pb push_back
#define lson (k<<1)
#define rson (k<<1|1)
#define lowbit(x) ((x)&-(x))
#define siz(x) ((int)(x).size())
#define all(x) (x).begin(),(x).end()
#define debug(x) cout<<#x<<" = "<<(x)<<endl
#define rep(i,s,t) for(register int i=(s),_t=(t);i<_t;++i)
#define per(i,s,t) for(register int i=(t)-1,_s=(s);i>=_s;--i)
using namespace std;
typedef long long ll;
typedef unsigned long long ull;
typedef unsigned int ui;
typedef double db;
typedef pair<int,int> pii;
typedef pair<ll,ll> pll;
typedef vector<int> veci;
const int inf=0x7fffffff,mod=(int)1e9+7,dxy[]={-1,0,1,0,-1};
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
inline void mod_add(int &a,int b,int m=mod){
	if((a+=b)>=m)a-=m;
}
inline void mod_minus(int &a,int b,int m=mod){
	if((a-=b)<0)a+=m;
}
int mod_pow(int a,int b,int m=mod){
	int res=1;
	for(;b;b>>=1,a=(ll)a*a%m)
		if(b&1)res=(ll)res*a%m;
	return res;
}
inline int calc_inv(int x,int m=mod){
	return mod_pow(x,m-2);
}


const int N=(int)1e6+5;

int mx,tot_edge,head[N],dep[N],cnt[N],stk[N],query[N];
ll ans[N];
pii edge[N];

void add_edge(int u,int v){
	edge[tot_edge]=pii(v,head[u]);
	head[u]=tot_edge++;
}

void dfs(int u){
	Max(mx,dep[u]);
	++cnt[dep[u]-1];
	for(int i=head[u];~i;i=edge[i].se){
		int v=edge[i].fi;
		dep[v]=dep[u]+1;
		dfs(v);
	}
}

inline bool check(int a,int b,int c){
	return (ll)(cnt[c]-cnt[a])*(a-b)<=(ll)(cnt[b]-cnt[a])*(a-c);
}

inline ll calc(int i,int K){
	return (ll)i*K+cnt[i];
}

inline int ceil(ll a,int b){
	return (a+b-1)/b;
}

int main(){
	int n,m;
	rd(n),rd(m);
	if(n==1){
		while(m--)puts("1");
		return 0;
	}
	int mx_query=0;
	rep(i,0,m){
		rd(query[i]);
		Max(mx_query,query[i]);
	}
	memset(head,-1,n+1<<2);
	for(int i=2,par;i<=n;++i){
		rd(par);
		add_edge(par,i);
	}
	dep[1]=1;
	dfs(1);
	per(i,1,mx)cnt[i]+=cnt[i+1];
	int tp=0;
	stk[tp++]=1,stk[tp++]=2;
	rep(i,3,mx+1){
		// y=i*x+cnt[i]
		for(;tp>1&&check(stk[tp-2],stk[tp-1],i);--tp);
		stk[tp++]=i;
	}
	for(int i=1,j=0;i<=mx_query;++i){
		for(;j<tp-1&&calc(stk[j],i)<=calc(stk[j+1],i);++j);
		ans[i]=calc(stk[j],i);
	}
	rep(i,0,m)pt(ceil(ans[query[i]],query[i])),putchar(" \n"[i==m-1]);
	return 0;
}

/*
	
	Oct.09.16
	
	Tags:tree,math,convex shell
	Submissions:2
	
	Memory 83604KB
	Time 540MS
	Code Length 3155B
	
*/

```