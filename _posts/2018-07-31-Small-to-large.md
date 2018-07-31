---
layout: post
title: "Small to large"
date: 2018-07-31 22:30:00
description: 'A primary tutorial for "Small to large".'
tags:
- graph
- tree
- data structure
categories:
- Algorithms
---

# Introduction

Small to large 算法可以用来解决树上的一些与子树内信息有关的问题.该算法实现相当简单,看似暴力却实则高效.

能使用这个算法的前提是:询问的是某些节点子树内的方便维护的信息,并且题目允许离线.

# Process

离线,通过一遍 $\text{dfs}$ 整棵树来处理所有询问. $\text{dfs}$ 的同时用一个数据结构 $D$ 来维护需要的信息.

$\text{dfs}$ 时,我们需要保证 $\text{dfs}$ 处理完节点 $u$ 后 $D$ 内恰好维护有 $u$ 子树中的信息.

对节点 $u$ 进行 $\text{dfs}$ 时:

1. 先递归对 $u$ 的每个轻儿子 $v$ 调用 $\text{dfs}$ ,并在回溯时将 $v$ 子树中的信息从 $D$ 中删去.
2. 然后对 $u$ 的重儿子调用 $\text{dfs}$ ,但在回溯时保留其子树中的信息.
3. 之后往 $D$ 中加入节点 $u$ 的信息及其所有轻儿子子树中的信息.此时 $D$ 中已维护好了子树 $u$ 的信息,可以回答相关询问.

上面说的加入和删除子树信息,指的就是暴力枚举子树中的所有节点进行处理.

# Code

```c++
void resume(int u)
{
	// TODO
}
void remove(int u)
{
	// TODO
}
void solve(int u)
{
	for(int i=lst[u];~i;i=edge[i].se)
	{
		int v=edge[i].fi;
		if(v==par[u]||v==heavy_son[u])continue;
		solve(v);
		rep(i,dfn[v],post[v]+1)remove(refer[i]);
	}
	if(heavy_son[u])solve(heavy_son[u]);
	for(int i=lst[u];~i;i=edge[i].se)
	{
		int v=edge[i].fi;
		if(v==par[u]||v==heavy_son[u])continue;
		rep(i,dfn[v],post[v]+1)resume(refer[i]);
	}
	resume(u);
	// answer relevant queries
}
```

# Analysis

首先分析一下算法的**时间复杂度**.

- 分析时间复杂度的关键在于分析 $\text{resume}$ 和 $\text{remove}$ 操作进行的次数.

- 注意到一个节点进行上述操作的次数分别等于它到根节点的路径上轻边的个数,而这是 $O(\lg n)$ 的,因此总操作次数为 $O(n \lg n)$ .
- 总时间复杂度即为 $O(n \lg n)$ 再乘上单次操作的复杂度.

再分析一下这个算法的一些**特点**.

- 用来解决一些与子树信息有关的问题.

- 算法必须离线.

- 需要能够高效地对数据结构加入和删除一个节点的信息.

- **因为实现时每次从数据结构中删除信息都会删除至整个数据结构完全为空为止,所以实际上只要能够高效清空数据结构即可,不需进行动态加入和删除.**

# Problem

[Codeforces 600E Lomsat gelral](http://codeforces.com/problemset/problem/600/E)

## Description

给定一棵 $n$ 个节点的根节点为 $1$ 的带点权树.

对每个节点,求其子树中出现次数最多的权值之和.

$n \le 10^5$ ,权值范围 $[1,n]$ .

## Solution

只要看懂了上面的内容就能轻松解决此题.

$O(n \lg n)$ .

## Code 

```c++
#include <bits/stdc++.h>
#define fi first
#define se second
#define debug(x) cerr<<#x<<" = "<<(x)<<endl
#define rep(i,s,t) for(int i=(s),_t=(t);i<_t;++i)
using namespace std;
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
// EOT


const int N=(int)1e5+5;

int mx,dfs_clock,lst[N],col[N],par[N],sz[N],heavy_son[N],dfn[N],post[N],refer[N],cnt[N];
pii edge[N<<1];
ll sum,ans[N];

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
	refer[dfn[u]=++dfs_clock]=u;
	sz[u]=1;
	for(int i=lst[u];~i;i=edge[i].se)
	{
		int v=edge[i].fi;
		if(v==par[u])continue;
		par[v]=u;
		dfs(v);
		sz[u]+=sz[v];
		if(!heavy_son[u]||sz[v]>sz[heavy_son[u]])heavy_son[u]=v;
	}
	post[u]=dfs_clock;
}

inline void remove(int u)
{
	--cnt[col[u]];
}

void resume(int u)
{
	int tar=col[u];
	++cnt[tar];
	if(cnt[tar]==mx)sum+=tar;
	if(cnt[tar]>mx)
	{
		mx=cnt[tar];
		sum=tar;
	}
}

void solve(int u)
{
	for(int i=lst[u];~i;i=edge[i].se)
	{
		int v=edge[i].fi;
		if(v==par[u]||v==heavy_son[u])continue;
		solve(v);
		rep(i,dfn[v],post[v]+1)remove(refer[i]);
		sum=mx=0;
	}
	if(heavy_son[u])solve(heavy_son[u]);
	for(int i=lst[u];~i;i=edge[i].se)
	{
		int v=edge[i].fi;
		if(v==par[u]||v==heavy_son[u])continue;
		rep(i,dfn[v],post[v]+1)resume(refer[i]);
	}
	resume(u);
	ans[u]=sum;
}

int main()
{
	int n;
	cin>>n;
	memset(lst,-1,n+1<<2);
	rep(i,1,n+1)rd(col[i]);
	for(int i=1,u,v;i<n;++i)
	{
		rd(u),rd(v);
		add_edges(u,v);
	}
	dfs(1);
	solve(1);
	rep(i,1,n+1)pt(ans[i]),putchar(" \n"[i==n]);
	return 0;
}

```

