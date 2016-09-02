---
layout: post
title: "计蒜客 微软项目经理的挑选方案"
date: 2016-07-07 21:46:00
description: 'Solution for...'
tags:
- dp
- data structure
- segment tree
categories:
- jisuanke
---

[**题目链接**](https://nanti.jisuanke.com/t/11218)

# 题目大意

给定$n$个互不相同的闭区间$[l_i,r_i]$,现在从中选出一些区间,满足对于任意未选出的区间,都有至少一个选出的区间与其有交集,求可能的方案数,对$10^9+7$取模.

$1\le n\le2\times10^5,1\le l_i<r_i\le10^9.$

# 题解

解题的方向肯定是先给区间排个序然后考虑dp.

我们将区间按左端点为第一关键词,右端点为第二关键词从小到大排序.

设第$i$个区间($1\le i\le n$)能**支配**的区间的极大区间是$[L[i],R[i]]$,满足$i\in[L[i],R[i]]$,且这个区间内的所有区间都与区间$i$有交集.
为什么叫"支配"呢?

因为如果选了区间$i$,那么它所支配的区间都**可以不选**了.

那么我们的目标就变成了需要**每个区间都被所选的区间支配到**.

似乎有点问题?

请看下面的手绘渣图:

![](http://kyleyoung-ymj.cf/assets/img/upload/jisuanke-select.png)

这样区间$i$往左边应该只能支配到$i$本身,但是如果选择了$i$,区间$i-2$也是可以不选的.

然鹅因为最终区间$i-1$也要被支配到,而这样区间$i-2$也会"顺便"被支配到,所以看作区间$i$无法支配到区间$i-2$也不会有影响.

关于如何求每个$L[i],R[i]$,我们可以转化为求区间$i$两侧最近的不能被$i$支配的区间.

为了防止下标越界,不妨在首尾加入两个区间$[0,0]$和$[\infty,\infty]$.

由于是按左端点排序,所以所有区间满足左端点单调递增,于是可以二分求$R[i]$.

而左边是求最大的下标$j$,使得区间$j$的右端点小于区间$i$的左端点,这个东西是**单调递增**的,可以用单调队列求解(当然也有线性的方法).

经过上面的分析,这个题目其实已经可以抛掉给出的区间不管了,完全转化成了序列上的dp问题.

接下来把原来的区间都看作点好了.

由于某种不可抗力,我们需要倒着dp(下面会解释原因).

令$dp[i][j]$表示考虑是否选编号为$i$~$n$的点,最后一个未被支配的点为$j$的方案数.

这样的状态定义是充分的,因为倒着来的话$L[i]$是单调递减的,那么如果选择当前的$i$点并且支配了最后一个未被支配的点$j$,此时最后一个未被支配的点就变为了$L[i]-1$,可以完成状态转移.

而如果正着来,就利用不了这个单调性,也就不好进行状态转移了([官方题解](http://blog.jisuanke.com/?p=229)中是一开始把区间按右端点当做第一关键词排序然后正着dp的,道理是一样的).


显然第一维$i$是可以去掉的.

dp的初始状态为$dp[n]=1$,要求的答案为$dp[0]$.

容易写出状态转移方程:

$$
dp[j]=
\begin{cases}
2\cdot dp[j] & R[i]<j\le n\\
2\cdot dp[j]+\sum_{k=L[i]}^{R[i]}dp[k] & j=L[i]-1\\
\end{cases}
$$

然后用线段树优化就可以了.

总的时间复杂度是$O(n\lg n)$.

---

补:后来发现其实转化后的问题就是选出一些区间覆盖所有的点,重新给区间排序也不会有影响.

# 代码

注:其实deque是系统关键词,比较危险,请不要学我...

```c++
#include<cstdio>
#include<cstring>
#include<cassert>
#include<algorithm>
#define lowbit(x) (x&-x);
#define fi first
#define se second
using namespace std;
typedef pair<int,int> pii;
const int N=(int)2e5+5,mod=(int)1e9+7,INF=0x7fffffff;
int n,L[N],R[N],deque[N],pow_2[N];
pii itv[N];
struct Segment_Tree{
	#define lson (k<<1)
	#define rson (k<<1|1)
	struct Node{
		int L,R,Lsh_cnt,sum;
	}tree[N<<2];
	void build(int L=0,int R=n,int k=1){
		tree[k].L=L;
		tree[k].R=R;
		tree[k].Lsh_cnt=0;
		tree[k].sum=R==n;
		if(L==R)return;
		int mid=L+R>>1;
		build(L,mid,lson);
		build(mid+1,R,rson);
	}
	inline void push_up(int k){
		if((tree[k].sum=tree[lson].sum+tree[rson].sum)>=mod)
			tree[k].sum-=mod;
	}
	inline void mod_Lsh(int &num,int cnt){
		num=1ll*num*pow_2[cnt]%mod;
	}
	void push_down(int k){
		if(!tree[k].Lsh_cnt)return;
		mod_Lsh(tree[lson].sum,tree[k].Lsh_cnt);
		mod_Lsh(tree[rson].sum,tree[k].Lsh_cnt);
		tree[lson].Lsh_cnt+=tree[k].Lsh_cnt;
		tree[rson].Lsh_cnt+=tree[k].Lsh_cnt;
		tree[k].Lsh_cnt=0;
	}
	int query_sum(int L,int R,int k=1){
		if(tree[k].L==L&&tree[k].R==R)
			return tree[k].sum;
		push_down(k);
		int mid=tree[k].L+tree[k].R>>1;
		if(R<=mid)return query_sum(L,R,lson);
		if(L>mid)return query_sum(L,R,rson);
		int res=query_sum(L,mid,lson)+query_sum(mid+1,R,rson);
		if(res>=mod)res-=mod;
		return res;
	}
	void modify(int tar,int val,int k=1){
		if(tree[k].L==tree[k].R){
			tree[k].sum=val;
			return;
		}
		push_down(k);
		int mid=tree[k].L+tree[k].R>>1;
		if(tar<=mid)modify(tar,val,lson);
		else modify(tar,val,rson);
		push_up(k);
	}
	void Lsh(int L,int R,int k=1){
		if(tree[k].L==L&&tree[k].R==R){
			mod_Lsh(tree[k].sum,1);
			++tree[k].Lsh_cnt;
			return;
		}
		push_down(k);
		int mid=tree[k].L+tree[k].R>>1;
		if(R<=mid)Lsh(L,R,lson);
		else if(L>mid)Lsh(L,R,rson);
		else{
			Lsh(L,mid,lson);
			Lsh(mid+1,R,rson);
		}
		push_up(k);
	}
}T;
void rd(int &res){
	res=0;
	char c;
	while(c=getchar(),c<48);
	do res=(res<<3)+(res<<1)+(c^48);
		while(c=getchar(),c>47);
}
void init(){
	itv[n]=pii(0,0);
	itv[n+1]=pii(INF,INF);
	sort(itv,itv+n+2);
	int head=0,tail=0;
	deque[tail++]=0;
	for(int i=1;i<=n;++i){
		for(int bin_L=i+1,bin_R=n+1;bin_L<=bin_R;){
			int mid=bin_L+bin_R>>1;
			if(itv[mid].fi>itv[i].se){
				R[i]=mid-1;
				bin_R=mid-1;
			}
			else bin_L=mid+1;
		}
		int bin_L=head,bin_R=tail-1,res;
		while(bin_L<=bin_R){
			int mid=bin_L+bin_R>>1;
			if(itv[deque[mid]].se<itv[i].fi){
				res=mid;
				bin_L=mid+1;
			}
			else bin_R=mid-1;
		}
		L[i]=deque[res]+1;
		head=res;
		while(head<tail&&itv[deque[tail-1]].se>=itv[i].se)--tail;
		deque[tail++]=i;
	}
	pow_2[0]=1;
	for(int i=1;i<=n;++i)
		if((pow_2[i]=pow_2[i-1]<<1)>=mod)pow_2[i]-=mod;
}
int main(){
	rd(n);
	for(int i=0;i<n;++i){
		rd(itv[i].fi);
		rd(itv[i].se);
	}
	init();
	T.build();
	for(int i=n;i;--i){
		if(R[i]<n)T.Lsh(R[i]+1,n);
		T.modify(L[i]-1,(1ll*T.query_sum(L[i],R[i])+(T.query_sum(L[i]-1,L[i]-1)<<1))%mod);
	}
	printf("%d\n",T.query_sum(0,0));
	return 0;
}
```
