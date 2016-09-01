---
layout: post
title: "计蒜客 百度地图的实时路况"
date: 2016-07-06 16:54:00
description: 'Solution for...'
tags:
- divide and conquer
- shortest path
categories:
- jisuanke
---

[**题目链接**](https://nanti.jisuanke.com/t/11217)

# 题目大意

给定一幅$n$个节点的有向图,定义$d(u,v,w)$为从点$u$出发,不经过点$v$,到达点$w$的最短路长度(如果不存在则为$-1$).

求$\sum_{y\neq x,y\neq z}d(x,y,z)$.

$4\le n\le300$,边权$0\le cost\le10^4$.

# 题解

暴力的做法就是枚举不经过的点$y$然后跑Floyd,复杂度是$O(n^4)$的.

实际上对于枚举的一个$y$,除了$y$之外其他所有的点都会作为Floyd的中间点枚举到,并且这个枚举的顺序不会影响算法的正确性,所以可以用一个奇妙的分治算法,一次用一个中间点去更新多个邻接矩阵的最短路.

这个分治不太好说清楚,但是看了代码就懂了.

复杂度是$O(n^3\lg n)$.

# 代码

```c++
#include<cstdio>
#include<cstring>
#include<cassert>
#include<iostream>
#include<algorithm>
using namespace std;
typedef long long ll;
const int N=305,LG=10,INF=1e9;
int n;
ll ans=0;
int mat[LG][N][N];
inline void Max(int &a,int b){
	if(b>a)a=b;
}
inline void Min(int &a,int b){
	if(a==-1||b<a)a=b;
}
void solve(int L,int R,int dep){
	if(L==R){
		for(int i=1;i<=n;++i){
			if(i==L)continue;
			for(int j=1;j<=n;++j){
				if(j==L)continue;
				ans+=mat[dep][i][j];
			}
		}
		return;
	}
	int mid=L+R>>1;
	for(int i=1;i<=n;++i)
		for(int j=1;j<=n;++j)
			mat[dep+1][i][j]=mat[dep][i][j];
	for(int k=mid+1;k<=R;++k)
		for(int i=1;i<=n;++i)
			for(int j=1;j<=n;++j)
				if(mat[dep+1][i][k]!=-1&&mat[dep+1][k][j]!=-1)
					Min(mat[dep+1][i][j],mat[dep+1][i][k]+mat[dep+1][k][j]);
	solve(L,mid,dep+1);
	for(int i=1;i<=n;++i)
		for(int j=1;j<=n;++j)
			mat[dep+1][i][j]=mat[dep][i][j];
	for(int k=L;k<=mid;++k)
		for(int i=1;i<=n;++i)
			for(int j=1;j<=n;++j)
				if(mat[dep+1][i][k]!=-1&&mat[dep+1][k][j]!=-1)
					Min(mat[dep+1][i][j],mat[dep+1][i][k]+mat[dep+1][k][j]);
	solve(mid+1,R,dep+1);
}
int main(){
	scanf("%d",&n);
	for(int i=1;i<=n;++i)
		for(int j=1;j<=n;++j){
			scanf("%d",&mat[0][i][j]);
		}
	solve(1,n,0);
	cout<<ans<<endl;
	return 0;
}
```
