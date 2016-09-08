---
layout: post
title: "入坑字符串之 后缀自动机"
date: 2016-09-08 11:08:00
description: 'A primary tutorial for Suffix Automaton.'
tags:
- string
- suffix automaton
categories:
- Algorithms
---

# Backgrounds

又滚回来学字符串了...

关于后缀自动机,似乎是在$2012$年冬令营由陈老师引进,并很快流行起来的一种强大的处理字符串的新兴数据结构.

个人感觉网上关于后缀自动机的资料并不是很完善,自学起来稍显吃力,于是决定~~入乡随俗~~记一些笔记.

后缀自动机的一个特点是性质一抓一大把,所以个人感觉直接去看代码的具体实现意义不大,还是需要一点一点自己理解和推导.

# References

- 《后缀自动机》
  - 陈立杰 $2012$年冬令营营员交流
- 《后缀自动机及其应用》
  - 张天扬 $2015$年国家队候选队员论文

# Notes

这里我们先说好,下面的笔记有部分属于本蒟蒻的个人见解,可能与论文中有所出入,如有不足请各位大神不吝赐教.

## Definition

一个字符串$S$的后缀自动机$(\text{Suffix Automaton,SAM})$是这样一台确定性有限自动机$(\text{Deterministic Finite Automaton,DFA})$,它可以接受且只能接受所有$S$的后缀,并且状态数和转移数都是最少的.

如果忽略字符集的大小,其状态数,转移数,以及构造的时间复杂度都是$O(n)$的,其中$n=\mid S\mid$.

## Primary Properties

我们暂时先不用去想象一台$\text{SAM}$究竟长什么样,而是单纯从理论的角度初步分析一些它的性质.

对于$S$的一个子串$str$,定义$right_{str}$表示$str$在$S$中出现的所有位置的右端点集合.那么$\mid right_{str}\mid$即为$str$在$S$中出现的次数.

有一个比较显然的性质,对于$S$中的两个不同子串$a,b$,它们的$right$集合要么交集为空集,要么一个是另一个的子集.因为如果它们交集不为空,那么一定一个子串是另一个的后缀,不妨令$a$是$b$的后缀,那么$right_a\supseteq right_b$.

因为$\text{SAM}$能且只能接受$S$的所有后缀,那么显然它能且只能识别$S$的所有子串.

考虑从自动机的初始态开始识别子串$str$到达的状态.从这个状态开始能够接受的非空字符串集合应该为$\lbrace s:\exists r\in right_{str},s.t. r+1<n,s=suf_{r+1}\rbrace$.就是说一个状态能够接受的字符串至于到达这个状态的子串的$right$集合有关.所以对于$S$的两个不同子串$a,b$,若$right_a=right_b$,则识别它们应该到达同一个状态.于是我们可以认为自动机上一个状态对应一个$right$集合,以及所有$right$集合等于这个集合的子串.下面对于自动机上的状态$s$,记$right_s$为其对应的$right$集合.

下面引入**$\text{parent}$树**.我们可以将后缀自动机上的所有状态视为树上的节点(下面不区分树上的节点与自动机上的状态),那么$\text{parent}$树是一棵以初始状态为根的有根树,其中一个非根节点$s$的父亲节点$par_s$是满足$right_s\subsetneqq right_{par_s}$,且$\mid right_{par_s}\mid$最小的状态(对于根节点,可以认为其对应的字符串为空串,对应的$right$集合为$[-1,n)$).这样显然可以构成一棵树对吧.

这里有几个关键点需要理解清楚.

1. $\text{parent}$树上的边与后缀自动机上的转移并没有什么直接联系.
2. $\text{parent}$树上的叶子节点并不一定对应自动机上的结束状态.
3. 基于前面关于$right$性质的讨论,可以知道$\text{parent}$树上$par_s$对应的字符串中任意一个都是$s$对应的字符串中任意一个的后缀.注意空串可以认为是任意串的后缀.
4. 树上叶子节点对应的$right$集合大小一定为$1$.不然,令$x$是叶子节点$right$集合中右端点的最大值,那么$right_{pre_x}=\lbrace x\rbrace$,对应的状态可以作为儿子节点,矛盾.


$\text{parent}$树在后缀自动机中十分重要,不管是分析性质,还是对后缀自动机进行构造,都离不开这棵树.

## Constructive Algorithm

下面考虑如何构造$\text{SAM}$.

### Analysis

考虑使用增量法,即每次通过在$pre_{n-2}$的$\text{SAM}$中插入一个字符$c=S_{n-1}$,从而构造出$pre_{n-1}$的$\text{SAM}$.

先考虑一下自动机上的转移与$right$集合有什么关系.如果状态$p$经过字符$c$能转移到状态$q$(不妨即为$trans(p,c)=q$),那么$right_q=\lbrace r+1:r\in right_p,s.t.r+1<n,S_{r+1}=c\rbrace$,且$right_q\neq\varnothing$.由此可以知道能够一步转移到状态$q$的字符$c$是唯一确定的.

插入新字符$c$时,显然要新增一个状态,令其为$np$,且$right_{np}=\lbrace n-1\rbrace$,并且还会有一些原本$right$集合不含$n-1$的状态,它们的$right$集合中会加入元素$n-1$.

考虑现在哪些状态的$right$集合中会包含$n-1$.除了初始状态之外,它们应该是一些$right$集合包含$n-2$的状态能转移到的状态.这些$right$集合包含$n-2$的状态在$\text{parent}$树上会形成一条一个节点到根节点的路径.我们记录一个状态$last$,它是插入字符$S_{n-2}$时新建的状态$np'$.那么这些状态就是$last$到根节点路径上的所有状态.

对于这些$right$集合包含$n-2$的状态,如果它没有经过$c$的转移,那么直接让它转移到$np$.否则,令$p$为路径上深度最深的,且$trans(p,c)\neq null$的状态,那么可以知道$p$的所有祖先节点都有经过$c$的转移.怎么处理这些状态,就比较复杂了,需要深入分析.

我们再引入一个变量$len_s$,表示状态$s$对应的最长子串的长度.我们知道,由一个状态的$right$集合和一个给定的长度$l$可以唯一确定一个串,使得其$right$集合恰好为这个状态的集合.当然这个$l$是有限制的,对于非根节点$s$,需满足$l\in(len_{par_s},len_s]$.从而可以知道$len_{par_s}<len_s$.

对于一个状态$q$和一个字符$c$,若$tran(p,c)=q$,那么所有这样的$p$在$\text{parent}$树上会是连续的祖先后代关系.且对其中深度最大的$p$,满足$len_q=len_p+1$.

若$trans(p,c)=q,trans(p',c)=q'\neq q$,且$p'$是$p$的祖先,那么不可能存在$p''\not\in path(p,root)$,满足$trans(p'',c)=q'$.

回到之前的讨论,令$p$是$last$到根的路径上第一个拥有经过$c$的转移的状态,它经过$c$转移到状态$q$(当然也可能不存在这样的$p$,这个也好处理,等下再侃),我们可以发现$len_q\ge len_p+1$.进行分类讨论:

1. $len_q=len_p+1$.那么所有经过$c$转移到$q$的状态都在$last$到根的这条路径上,即它们的$right$集合都包含$n-2$,那么直接将$right_q$加上$n-1$,不会有问题.并且基于前面的讨论,路径上深度更浅的状态也不用再考虑了.
2. $len_q>len_p+1$.这时候考虑能经过$c$转移到$q$的所有状态,它们会有一些不在$last$到根的这条路径上.所以这时直接将$right_q$加上$n-1$就会出现问题,而解决方法是再新建一个状态$nq$(也可以理解为让$q$分裂为两个状态),且$right_{nq}=right_q\cup\lbrace n-1\rbrace$,它的其它属性($par_{nq}$以及转移)与$q$相同.那么让这条路径上转移到$q$的状态改为转移到$nq$即可.然后我们也不用再考虑更上面的状态了.

### Algorithm

下面整理一下构造算法的实现流程.

首先新建初始状态$init$,$par_{init}=null,len_{init}=0$.令$last=init$.

依次插入串$S$的每个字符$S_i=c$.

每次新建状态$np,len_{np}=i+1$.对于$last$到根的路径上没有经过$c$的转移的状态,让它们转移到$np$.找到路径上最深的$p,trans(p,c)=q\neq null$.

1. 若不存在这样的$p$,则$par_{np}=init$.
2. $len_q=len_p+1$.直接令$par_{np}=q$.
3. $len_q>len_p+1$.新建状态$nq,par_{nq}=par_q,len_{nq}=len_p+1$,$nq$拥有与$q$相同的转移.令$par_{np}=par_q=nq$.对于路径上满足$trans(p',c)=q$的所有$p'$,令$trans(p',c)=nq$.

令$last=np$.

### Complexity

容易看出后缀自动机的状态数是$O(n)$的.

转移数和构造的复杂度应该也是$O(n)$的,但是我暂时没有理解.如果哪位大神有详细证明请一定告诉本蒟蒻,感激不尽.

### Code

代码实现非常简单.需要注意的是节点个数要开到字符串长度的两倍.

如果字符集太大,可以考虑用其它数据结构存储转移.

```c++
const int N=(int)1e5+5;
char str[N];
struct Suffix_Automaton{
	static const int NODE=N<<1,C=26;
	int allc,par[NODE],len[NODE],trans[NODE][C];
	int NewNode(){
		int ret=++allc;
		memset(trans[ret],0,C<<2);
		return ret;
	}
	void cons(){
		allc=0;
		int last=NewNode();
		par[last]=len[last]=0;
		rep(i,0,strlen(str)){
			int c=str[i],p=last,np=NewNode();
			len[np]=i+1;
			for(;p&&!trans[p][c];p=par[p])trans[p][c]=np;
			if(!p)par[np]=0;
			else{
				int q=trans[p][c];
				if(len[q]==len[p]+1)par[np]=q;
				else{
					int nq=++allc;
					par[nq]=par[q];
					len[nq]=len[p]+1;
					memcpy(trans[nq],trans[q],C<<2);
					par[np]=par[q]=nq;
					for(;p&&trans[p][c]==q;p=par[p])trans[p][c]=nq;
				}
			}
			last=np;
		}
	}
};
```


## Applications

后缀自动机的强大之处在于能够灵活处理各种子串问题,而且它有两个形态,作为自动机它是一个$\text{DAG}$,而其背后又隐藏着一棵$\text{parent}$树,可以在这两个特殊的图结构上大做文章.

解决问题时有时需要回到上面分析的各种性质中去思考.

限于篇幅,本篇博客就到这里结束了.更多的请参考论文以及网上的资料.