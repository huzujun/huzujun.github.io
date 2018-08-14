---
layout: post
title: "Deep-Shallow-Decomposition"
date: 2018-07-31 22:30:00
description: 'A primary tutorial for "Deep-Shallow-Decomposition".'
tags:
- graph
- tree
categories:
- Algorithms
---

# Introduction

长链剖分是树链剖分的一种.

它与重链剖分的区别就是将重儿子改为"长儿子",即子树中节点的最大深度(子树的高度)最大的儿子.

# Problems

## 1. 求 $K$ 级祖先

### Description

给定一棵 $n$ 个节点的有根树, $m$ 次询问,每次询问给出 $u,K$ ,求节点 $u$ 的 $K$ 级祖先.

### Solution

这里介绍一个用到长链剖分的单次询问 $O(1)$ 的方法.

预处理部分:

1. 先来一波长链剖分,记录下每个点所在链的链头.
2. 对每条长链,记它的长度为 $\text{len}$ ,那么记录下链头的前 $\text{len}$ 级祖先,并且记录下链头向下的前 $\text{len}$ 个链中的元素.因为所有链的长度之和为 $n$ ,因此这部分复杂度为 $O(n)$ .
3. 倍增预处理出每个节点的 $2^k$ 级祖先,这部分时空复杂度都为 $O(n \lg n)$ .
4. 预处理出 $[1,n]$ 范围内的数的二进制最高位的值,即 $2^{\lfloor \log_2 k \rfloor}(1 \le k \le n)$ .

询问部分:

1. 利用倍增数组往上跳 $2^{\lfloor \log_2 K \rfloor}$ 层,记到达的节点为 $v$ ,剩余层数为 $K'$ .
2. 注意到从 $u$ 到 $v$ 跳的层数一定大于 $K'$ ,因此 $v$ 所在长链的长度一定大于 $K'$ .因此不管要求的点在  $v$ 所在链头的上面还是下面都可以用预处理的第 $2$ 部分的数据直接回答询问.

于是该算法的预处理时空复杂度均为 $O(n \lg n)$ ,单次询问复杂度为 $O(1)$ .

### Code

```c++
#include <bits/stdc++.h>
#define fi first
#define se second
#define pb push_back
#define debug(x) cerr<<#x<<" = "<<(x)<<endl
#define rep(i,s,t) for(int i=(s),_t=(t);i<_t;++i)
using namespace std;
typedef vector<int> veci;
typedef pair<int,int> pii; 
typedef long long ll;
void rd(int &x)
{
	x=0;
	char c;
	while(c=getchar(),c<48);
	do x=x*10+(c^48);
		while(c=getchar(),c>47);
}
template<class T>void rec_pt(T x)
{
	if(!x)return;
	rec_pt(x/10);
	putchar(x%10^48);
}
template<class T>void pt(T x)
{
	if(!x)putchar('0');
	else rec_pt(x);
}
template<class T>inline void ptn(T x)
{
	pt(x),putchar('\n');
}
template<class T>inline void Max(T &a,T b)
{
	if(b>a)a=b;
}
template<class T>inline void Min(T &a,T b)
{
	if(b<a)a=b;
}
// EOT


const int N=(int)3e5+5,LG=20;

int lst[N],par[N],dep[N],depth[N],deep_son[N],tp[N],highbit[N],parent[LG][N];
pii edge[N<<1];
veci up[N],down[N];

void add_edge(int u,int v)
{
	static int tot_edge;
	edge[tot_edge]=pii(v,lst[u]);
	lst[u]=tot_edge++;
}

inline void add_edges(int u,int v)
{
	add_edge(u,v),add_edge(v,u);
}

void dfs(int u)
{
	depth[u]=1;
	for(int i=lst[u];~i;i=edge[i].se)
	{
		int v=edge[i].fi;
		if(v==par[u])continue;
		par[v]=u;
		dep[v]=dep[u]+1;
		dfs(v);
		Max(depth[u],depth[v]+1);
		if(!deep_son[u]||depth[v]>depth[deep_son[u]])deep_son[u]=v; 
	}	
}

void redfs(int u)
{
	if(deep_son[u])
	{
		tp[deep_son[u]]=tp[u];
		redfs(deep_son[u]);
	}
	for(int i=lst[u];~i;i=edge[i].se)
	{
		int v=edge[i].fi;
		if(v==par[u]||v==deep_son[u])continue;
		tp[v]=v;
		redfs(v);
	}
}

int query(int u,int K)
{
	u=parent[highbit[K]][u];
	if(!(K-=1<<highbit[K]))return u;
	int delta=dep[u]-dep[tp[u]];
	if(delta<K)return up[tp[u]][K-delta];
	return down[tp[u]][delta-K];
}

int main()
{
	int n,m;
	cin>>n>>m;
	memset(lst,-1,n+1<<2);
	for(int i=1,u,v;i<n;++i)
	{
		rd(u),rd(v);
		add_edges(u,v);
	}
	dfs(1);
	redfs(tp[1]=1);
	rep(i,1,n+1)if(tp[i]==i)
	{
		up[i].resize(depth[i]+1);
		down[i].resize(depth[i]+1); 
		for(int u=i,cnt=0;u&&cnt<=depth[i];u=par[u],++cnt)up[i][cnt]=u;
		for(int u=i,cnt=0;cnt<=depth[i];u=deep_son[u],++cnt)down[i][cnt]=u;
	}
	rep(u,1,n+1)parent[0][u]=par[u];
	rep(i,1,LG)rep(u,1,n+1)parent[i][u]=parent[i-1][parent[i-1][u]];
	for(int i=1,j=0;i<=n;++i)
	{
		if(i==1<<j+1)++j; 
		highbit[i]=j;
	}
	for(int u,K;m--;)
	{
		rd(u),rd(K);
		ptn(query(u,K));
	}
	return 0;
}

```

## 2.Dominant Indices

### Source

[Codeforces 1009 F](http://codeforces.com/problemset/problem/1009/F)

### Description

给定一棵 $n$ 个节点的有根树.

对任意节点 $u$ ,定义 $d_{u,i}$ 为子树 $u$ 中与 $u$ 的距离恰为 $i$ 的节点个数.

对任意节点 $u$ ,定义其支配值为下标 $j$ ,满足:

- $\forall k < j,d_{u,k}<d_{u,j}.$
- $\forall k > j,d_{u,k} \le d_{u,j}.$

要求求出所有节点的支配值.

$n \le 10^6.$

### Solution

考虑对所有节点都求出其所有的 $d$ 值.

计算的思路是先递归计算出孩子节点的 $d$ 数组,然后利用这些值计算出当前节点的 $d$ 数组.

注意到 $d$ 数组的元素个数为节点的子树高度,我们计算完 $u$ 的所有孩子节点之后可以直接将其长儿子的 $d$ 数组保留下来,在最前面增加一个 $d_{u,0}=1$ 作为 $u$ 的 $d$ 数组,不必重新进行赋值,这部分复杂度为 $O(1)$ .然后将 $u$ 的其它孩子的 $d$ 数组暴力合并到该数组中同时维护要求的答案.

注意到暴力合并数组的操作只会在长链的顶端发生一次,并且合并的复杂度与数组的长度(即长链的长度)成线性关系,因此上述算法总的时空复杂度均为 $O(n)$ .

具体实现时,在数组最前面加一个元素可以通过将数组倒过来维护或者预留好数组的空间来实现.

### Code

```c++
#include <bits/stdc++.h>
#define fi first
#define se second
#define lson (k<<1)
#define rson (k<<1|1)
#define pb push_back
#define lowbit(x) ((x)&-(x))
#define rep(i,s,t) for(int i=(s),_t=(t);i<_t;++i)
#define per(i,s,t) for(int i=(t)-1,_s=(s);i>=_s;--i)
#define debug(x) cerr<<#x<<" = "<<(x)<<endl
using namespace std;
typedef long long ll;
typedef vector<int> veci;
typedef pair<int,int> pii;
template<class T>void rd(T &x)
{
	x=0;
	char c;
	while(c=getchar(),c<48);
	do x=x*10+(c^48);
		while(c=getchar(),c>47);
}
template<class T>void rec_pt(T x)
{
	if(!x)return;
	rec_pt(x/10);
	putchar(x%10^48);
}
template<class T>void pt(T x)
{
	if(!x)putchar('0');
	else rec_pt(x);
}
template<class T>inline void ptn(T x)
{
	pt(x),putchar('\n');
}
template<class T>inline void Max(T &a,T b)
{
	if(b>a)a=b;
}
template<class T>inline void Min(T &a,T b)
{
	if(b<a)a=b;
}
int gcd(int a,int b)
{
	return b?gcd(b,a%b):a;
}
// EOT


const int N=(int)1e6+5;

int tot_edge,lst[N],par[N],dep[N],depth[N],deep_son[N],tp[N];
pii edge[N<<1];
int ans[N],memo[N],*allc=memo,*f[N];

void add_edge(int u,int v)
{
	edge[tot_edge]=pii(v,lst[u]);
	lst[u]=tot_edge++;
}

inline void add_edges(int u,int v)
{
	add_edge(u,v),add_edge(v,u);
}

void dfs(int u)
{
	for(int i=lst[u];~i;i=edge[i].se)
	{
		int v=edge[i].fi;
		if(v==par[u])continue;
		par[v]=u;
		dep[v]=dep[u]+1;
		dfs(v);
		if(!deep_son[u]||depth[v]>depth[deep_son[u]])deep_son[u]=v;
	}
	if(deep_son[u])depth[u]=depth[deep_son[u]]+1;
}

void allc_dfs(int u)
{
	if(deep_son[u])
	{
		tp[deep_son[u]]=tp[u];
		allc_dfs(deep_son[u]);
		for(int i=lst[u];~i;i=edge[i].se)
		{
			int v=edge[i].fi;
			if(v==par[u]||v==deep_son[u])continue;
			allc_dfs(tp[v]=v);
		}
	}
	else
	{
		int sz=dep[u]-dep[tp[u]]+1;
		f[u]=allc+=sz;
	}
}

void DP(int u)
{
	if(!deep_son[u])
	{
		f[u][0]=1;
		return;
	}
	for(int i=lst[u];~i;i=edge[i].se)
	{
		int v=edge[i].fi;
		if(v==par[u])continue;
		DP(v);
	}
	f[u]=f[deep_son[u]]-1;
	f[u][0]=1;
	if(f[deep_son[u]][ans[deep_son[u]]]>1)ans[u]=ans[deep_son[u]]+1;
	for(int i=lst[u];~i;i=edge[i].se)
	{
		int v=edge[i].fi;
		if(v==par[u]||v==deep_son[u])continue;
		rep(j,1,depth[v]+2)
		{
			f[u][j]+=f[v][j-1];
			if(f[u][j]>f[u][ans[u]])ans[u]=j;
			else if(f[u][j]==f[u][ans[u]])Min(ans[u],j);
		}
	}
}

int main()
{
	int n;
	rd(n);
	memset(lst,-1,n+1<<2);
	for(int i=1,u,v;i<n;++i)
	{
		rd(u),rd(v);
		add_edges(u,v);
	}
	dfs(1);
	allc_dfs(tp[1]=1);
	DP(1);
	rep(i,1,n+1)ptn(ans[i]);
	return 0;
}
```
