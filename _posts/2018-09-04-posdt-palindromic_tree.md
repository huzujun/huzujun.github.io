---
layout: post
title: "回文自动机实现及模板"
date: 2018-09-04 22:42:21
image: '/assets/img/'
description: 2018南京网络赛中出现了，当场粗略学来用，赛后将其模板化
tags:
- 回文自动机
categories:
- acm
---
## 概述
回文自动机（简称PAM，又称回文树，Palindromic Tree）是一种用于处理回文串的结构，在其结构内可以找到原串中的所有回文子串，经由APIO2014推广。下面对其原理及实现进行介绍。
本文中所有图片来自网络，感谢其作者的贡献。

## 结构
![结构](https://ksmeow.moe/wp-content/uploads/2018/06/palind_tree_struct.jpg)
如图为串abba的PAM示意图，其中，实线表示回文串的扩展转移边，虚线表示后缀链接。

### 节点信息
每个节点对应一个唯一的回文子串。

* 回文串出现的次数cnt(u)
* 回文串的长度len(u)
* 可以不保存具体的字符串信息（有事也需要保存，如2018南京网络赛I题）

为了方便，我们规定，偶回文树树根len(rt0)=0，奇回文树树根len(rt1)=−1。


### 转移边
转移边u→v对节点的意义是，将u对应的回文串左右两边加上同一个字符c得到节点v对应的回文串。

### 失配边
指向该节点对应子串的最长回文后缀

为了方便我们规定偶回文树树根rt0指向奇回文数根rt1

### 节点信息转移

* 记对应回文串出现的次数cnt(u),则
$$cnt(u)=\sum _{v=son(u)} {cnt(v)}$$

* 记对应回文串的长度len(u)，若节点v是由节点u转移而来的，则有len(v)=len(u)+2


## 构造
和后缀自动机类似，用增量法构造，每次在母串后插入一个字符，更新PAM的复杂度是O(1)的，因此构造自动机是O(n)的。

我们记录上一次插入节点的位置last，对于新插入的字符c，我们检查之前的最长回文后缀的前一个字母是否和c相同，如果相同，那么就创建一个新的节点，否则我们沿着适配边找最长回文后缀的最长回文后缀（稍微有点绕口）
如当前的母串是 
$$caba$$
现在我要插入新的字符 b

那么我先看到最长回文后缀aba的前一个字母是c，和新插入的字符a不同（因此cabab不能组成回文子串），因此我沿着失配边找到aba的最长回文后缀a，由于前一个字符b与新插入的字符相同，能组成新回文子串aba，因此我新建节点。

## 模板
具体实现看代码吧
```c++
#include <bits/stdc++.h>
#define rep(i, a, b) for (int i=a; i<=b; i++)
#define drep(i, a, b) for (int i=a; i>=b; i--)
#define inf 1e9
using namespace std;
typedef long long ll;
const int maxn=300010;
char s[maxn];
int n;
struct Ptree {
    int last;
    struct Node {
        int cnt, lenn, fail, son[27];
        Node(int lenn, int fail):lenn(lenn), fail(fail), cnt(0){
            memset(son, 0, sizeof(son));
        };
    };
    vector<Node> st;
    inline int newnode(int lenn, int fail=0) {
        st.emplace_back(lenn, fail);
        return st.size()-1;
    }
    inline int getfail(int x, int n) {
        while (s[n-st[x].lenn-1] != s[n]) x=st[x].fail;
        return x;
    }
    inline void extend(int c, int i) {
        int cur=getfail(last, i);
        if (!st[cur].son[c]) {
            int nw=newnode(st[cur].lenn+2, st[getfail(st[cur].fail, i)].son[c]);
            st[cur].son[c]=nw;
        }
        st[ last=st[cur].son[c] ].cnt++;
    }
    void init() {
        scanf("%s", s+1);
        n=strlen(s+1);
        s[0]=0;
        newnode(0, 1), newnode(-1);
        last=0;
        rep(i, 1, n) extend(s[i]-'a', i);
    }
    ll count() {
        drep(i, st.size()-1, 0) st[st[i].fail].cnt+=st[i].cnt;
        ll ans=0;
        rep(i, 2, st.size()-1) ans=max(ans, 1LL*st[i].lenn*st[i].cnt);
        return ans;
    }
}T;
int main() {
    T.init();
    printf("%lld\n", T.count());
}
```
稍微注意的是s[0]赋值为一个特殊字符，方便处理

## 2018南京网络赛I题skr
https://nanti.jisuanke.com/t/30998

    A number is skr, if and only if it's unchanged after being reversed. For example, "12321", "11" and "1" are skr numbers, but "123", "221" are not. FYW has a string of numbers, each substring can present a number, he wants to know the sum of distinct skr number in the string. FYW are not good at math, so he asks you for help.
    
	the length of SS is less than 2000000.

这题的话稍微改改模板就好了，每个节点维护一个num表示节点对应的树，新建节点的时候转移一下就好了

```c++
#include <bits/stdc++.h>
#define rep(i, a, b) for (int i=a; i<=b; i++)
#define drep(i, a, b) for (int i=a; i>=b; i--)
#define inf 1e9
using namespace std;
typedef long long ll;
const ll mod=1e9+7;
const int maxn=2001000;
char s[maxn];
ll ten[maxn];
int n;
struct Ptree {
    int last;
    struct Node {
        int cnt, lenn, fail, son[10];
        ll num;
        Node(int lenn, int fail):lenn(lenn), fail(fail), cnt(0){
            memset(son, 0, sizeof(son));
        };
    };
    vector<Node> st;
    inline int newnode(int lenn, int fail=0) {
        st.emplace_back(lenn, fail);
        return st.size()-1;
    }
    inline int getfail(int x, int n) {
        while (s[n-st[x].lenn-1] != s[n]) x=st[x].fail;
        return x;
    }
    inline void extend(int c, int i) {
        int cur=getfail(last, i);
        if (!st[cur].son[c]) {
            int nw=newnode(st[cur].lenn+2, st[getfail(st[cur].fail, i)].son[c]);
            if (st[nw].lenn == 1) st[nw].num=c;
            else if (st[nw].lenn == 2) st[nw].num=c*10+c;
            else st[nw].num=((c+st[cur].num*10)%mod+c*ten[st[cur].lenn+1])%mod;
            st[cur].son[c]=nw;
        }
        st[ last=st[cur].son[c] ].cnt++;
    }
    void init() {
        scanf("%s", s+1);
        n=strlen(s+1);
        s[0]=0;
        newnode(0, 1), newnode(-1);
        last=0;
        ten[0]=1;
        rep(i, 1, maxn-1) ten[i]=ten[i-1]*10%mod;
        rep(i, 1, n) extend(s[i]-'0', i);
    }
    ll count() {
        ll ans=0;
        rep(i, 2, st.size()-1) ans=(ans+st[i].num)%mod;
        return ans;
    }
}T;
int main() {
    T.init();
    printf("%lld\n", T.count());
}
```