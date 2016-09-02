---
layout: post
title: "Codeforces70E Information Reform"
date: 2016-06-05 19:09:00
description: 'Solution for Codeforces70E.'
tags:
- dp
- tree
categories:
- Codeforces
---

[**题目链接**](http://www.codeforces.com/contest/70/problem/E)

# 题目大意

给定一棵$n$个节点的边权均为$1$的树,现在要选择至少一个节点建立中转站,建立中转站的代价为$K$,其它非中转站节点$u$的代价为$cost_{dist(u,v)}$,其中$v$是与$u$距离最小的中转站节点.求总的最小代价以及任意一种最优方案.

$n\leqslant 180.$

# 解题流程

显然树形dp,然而并不会状态定义.

题解不知道在说什么.

参考了[一份比较简洁的代码](http://www.codeforces.com/contest/70/submission/18031431).

# 题解

一般来说树形dp状态定义的关键就是把一棵子树的状态压到一个点上.然而因为子树中的点可能往上面走,状态很难封闭起来.

有一种神奇的状态定义:$dp[cur][i]$表示如果$i$节点必须建一个中转站,以$cur$为根的子树中所有节点所需的代价之和(为了方便转移建立$i$这个中转站的代价$K$可以先不计入).

我们再设$opt[cur]$为使$dp[cur][i]$最小的一个$i$.

然后考虑状态转移.计算$dp[cur][i]$时,对每棵以$cur$的儿子节点$son$为根的子树,要么代价直接就是$dp[son][i]$,要么再选一个点$j$建立中转站,代价就是$dp[son][j]+K$.而这里的$j$显然就是$opt[son]$.

于是可以得到状态转移方程:

$$dp[cur][i]=cost[dist[cur][i]]+\sum min(dp[son][i],dp[son[opt[son]]+K).$$

似乎有点问题?

这样是不是把不是建在子树内的中转站的代价也算进去了?

而且离$cur$最近的中转站也不一定是$i$了?

确实是这样.

但是对于最优解,这样转移是不会出错的,即最终结果不会错.

有时候dp状态的转移需要严格,而也会有像这样可能错误转移,但不会影响答案的情况.

于是复杂度可以做到$O(n^3)$.

最后还要处理一下离每个点最近的中转站.

另外因为$n$很小,两点间的距离可以直接Floyd跑.

# 代码君 

```c++
#include<cstdio>
#include<vector>
#include<algorithm>
using namespace std;
const int N=185,INF=1e9;
int n,K,cost[N],dist[N][N],dp[N][N],opt[N],belong[N];
vector<int>T[N];
inline void Min(int &a,int b){
	if(b<a)a=b;
}
void Floyd(){
	for(int k=1;k<=n;++k)
		for(int i=1;i<=n;++i)
			for(int j=1;j<=n;++j)
				Min(dist[i][j],dist[i][k]+dist[k][j]);
}
void DP(int cur,int par){
	for(int i=1;i<=n;++i)
		dp[cur][i]=cost[dist[cur][i]];
	for(int i=0;i<T[cur].size();++i){
		int son=T[cur][i];
		if(son==par)continue;
		DP(son,cur);
		for(int i=1;i<=n;++i)
			dp[cur][i]+=min(dp[son][i],dp[son][opt[son]]+K);
	}
	opt[cur]=-1;
	for(int i=1;i<=n;++i)
		if(opt[cur]==-1||dp[cur][i]<dp[cur][opt[cur]])opt[cur]=i;
}
void assign(int cur,int par,int tar){
	belong[cur]=tar;
	for(int i=0;i<T[cur].size();++i){
		int son=T[cur][i];
		if(son==par)continue;
		assign(son,cur,dp[son][opt[son]]+K<dp[son][tar]?opt[son]:tar);
	}
}
int main(){
	scanf("%d%d",&n,&K);
	for(int i=1;i<=n;++i)
		T[i].clear();
	for(int i=1;i<=n;++i)
		for(int j=1;j<=n;++j)
			dist[i][j]=(i!=j)*INF;
	cost[0]=0;
	for(int i=1;i<n;++i)
		scanf("%d",&cost[i]);
	for(int i=1,u,v;i<n;++i){
		scanf("%d%d",&u,&v);
		dist[u][v]=dist[v][u]=1;
		T[u].push_back(v);
		T[v].push_back(u);
	}
	Floyd();
	DP(1,0);
	assign(1,0,opt[1]);
	printf("%d\n",dp[1][opt[1]]+K);
	for(int i=1;i<=n;++i)
		printf("%d%c",belong[i],i==n?'\n':' ');
	return 0;
}
/*
	
	Jun.05.16
	
	Tags:dp
	Submissions:1
	
	Time 62ms
	Memory 2400KB
	
*/
```
