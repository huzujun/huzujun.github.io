---
layout: post
title: "入坑字符串之 后缀数组"
date: 2016-07-27 10:32:00
description: 'A primary tutorial for Suffix Array.'
tags:
- string
- suffix array
- data structure
- sparse table
categories:
- Algorithms
---

# 学习背景

据说后缀数组是"处理字符串的有力工具"对吧...
之前学字符串时练了KMP和AC自动机什么的就把后缀数组坑了...
然而多校肯定会出字符串的题,所以现在来看这个略奥妙的东西.

# 算法资料

- 《后缀数组——处理字符串的有力工具》
  - by 罗穗骞 2009年国家集训队论文
- 《算法竞赛入门经典——训练指南》

# 学习笔记

首先说一下...学这个还是得静下心来自己看论文...这篇论文非常良心orz神犇罗穗骞orz
然而论文上的代码不太符合我的代码习惯...有同感的建议适当参考训练指南上的代码,因为有注释比较好看懂.

## 构造

先整理一下一些重要的概念:

- **后缀数组**$sa[i]$表示的是将字符串的每个后缀按字典序从小到大排序后第$i$小的后缀的起始下标.
- **名次数组**$rk[i]$(温馨提示:$rank$是系统关键词)表示后缀$i$在排好序的后缀中排第几小.
- **高度数组**$height[i]$表示$\text{suffix}(sa[i-1])$与$\text{suffix}(sa[i])$的$\text{lcp}$(最长公共前缀).

一般我们要用的也就是这三个数组了.

这里要注意下标从$0$开始还是从$1$开始的问题,当然两者都是可以的,但因为代码习惯和具体实现的关系,我一般是将字符串的下标从$0$开始,将排完序的名次从$1$开始.

构造后缀数组,一般来说用$O(n\lg n)$的**倍增算法**就足够了~~(懒得学DC3)~~.

具体的实现过程和优化就不细讲了,还是说说容易错的细节:

首先为了防止数组越界我们可以预先在字符串后面补上一个小于出现过的所有字符的特殊字符,这样它的字典序是所有后缀中最小的,所以构造时名次的下标可以从$0$开始.

在构造的过程中我们一般使用的是**基数排序**.注意如果一开始字符的范围很大,讲道理可以将其改为快速排序,但似乎有点小麻烦,所以直接离散来做也是可以的.

搞出后缀数组后就可以$O(n)$求出名次数组和高度数组了.

还有,严格来讲$height$数组的下标应该从$2$开始,但是一般将$height[1]$看做$0$也不会有问题,大多数模板求出来也会是这样.

贴一份整理了好久才弄出来的模板:

```c++
char str[N];
struct Suffix_Array{
    int n,sa[N],rk[N],height[N],cnt[N];
    void cons(){
        int m='z'+1,*a=rk,*b=height;
        str[(n=strlen(str))++]='#';
        memset(cnt,0,m<<2);
        rep(i,0,n)++cnt[a[i]=str[i]];
        rep(i,1,m)cnt[i]+=cnt[i-1];
        per(i,0,n)sa[--cnt[a[i]]]=i;
        for(int j=1,p=0;p<n;j<<=1,m=p){
            p=0;
            rep(i,n-j,n)b[p++]=i;
            rep(i,0,n)if(sa[i]>=j)b[p++]=sa[i]-j;
            memset(cnt,0,m<<2);
            rep(i,0,n)++cnt[a[b[i]]];
            rep(i,1,m)cnt[i]+=cnt[i-1];
            per(i,0,n)sa[--cnt[a[b[i]]]]=b[i];
            swap(a,b);
            p=1;
            a[sa[0]]=0;
            rep(i,1,n)a[sa[i]]=b[sa[i-1]]==b[sa[i]]&&b[sa[i-1]+j]==b[sa[i]+j]?p-1:p++;
        }
        --n;
        if(a!=rk)memcpy(rk,a,n+1<<2);
        for(int i=0,h=0;i<n;height[rk[i++]]=h){
            if(h)--h;
            for(int j=sa[rk[i]-1];str[i+h]==str[j+h];++h);
        }
    }
}SA;
```

裸题[戳这](http://uoj.ac/problem/35).

## 应用

初学时可能不太明白这样给后缀排序后有什么用,然而看了论文之后就发现...后缀数组真是太强大了!!

后缀数组的所有应用都是基于这么一句看似简单却又十分精髓的话:

**一个字符串的任何子串都可以看做是它后缀的一个前缀.**

基于这个想法,我们就可以巧妙利用$height$数组求各种各样的最长子串问题了.

论文上给出的例题还是很全面的,也介绍了许多经典的用后缀数组解题的**技巧和套路**:

### RMQ

求字符串的两个后缀的$\text{lcp}$,就是在$height$数组上的一个RMQ问题.

后缀$i$和后缀$j(rk[i]<rk[j])$的$\text{lcp}$,就等于$\min\limits_{rk[i]<k\le rk[j]}(height[k])$.

注意一定要把下标映射到$rk$数组上来求RMQ.

具体实现,一般是用ST表来$O(n\lg n)$预处理,然后$O(1)$查询.

### 二分答案

本不用多讲的,二分的思想自然哪里都有,但是这里用得特别多,所以提一下.

求最长的什么子串,这个一般来说是显然满足二分的性质的,而且转为判定性问题后会容易许多,所以经常在后缀数组题目里出现.

> "后缀数组的题搞不了就二分答案,肯定可以搞,大不了多个$\lg$而已."——ShinFeb

再次膜拜~~立flag~~神犇ShinFeb orz

### 给高度数组分组

二分答案后,很容易给高度数组分组,使得只有同一个组内的两个后缀的$\text{lcp}$大于等于二分的$len$.然后就可以在组内考虑其他的限制能否满足了.

### 拼接字符串

当然后缀数组也可以用来解决多个字符串的问题,方法是将多个字符串拼接起来,再对形成的新的字符串求后缀数组.

为了方便,我们一般会用不同的没有出现过的字符来连接每两个字符串.

在处理时要特别注意求出来的子串可能是出现在原来的一个字符串之中,以及类似的比较坑的情况.

当然在一些情况下我们也需要把一个字符串本身翻转过来接在它后面之类的.

差不多就是这些了吧.

# 练习

来看看论文上几道比较难的题(当然都很经典).

## POJ3693 Maximum repetition substring

[题目链接](http://poj.org/problem?id=3693)

### 题目大意

给定一个字符串,求重复次数最多的连续重复子串,输出字典序最小的解.

字符串长度$n\le 10^5$,只包含小写字母.

### 题解

直接求不好求,考虑枚举连续重复子串的循环节的长度$L$,然后求最大重复次数(这里只考虑重复至少$2$次的情况),那么这个子串肯定经过了某两个$str[k\cdot L]$及$str[(k+1)L]$.对每个$L$再枚举这个$k$,这样枚举的复杂度是$O(n\ln n)$(实际上$L$显然只用枚举到$\lfloor\frac n2\rfloor$).

$$
\dots\{ab\}(\underline a\{ab\}[\underline aaba)aba]\dots
$$

看上面这幅图(学名$\LaTeX$),画下划线的是当前枚举的两个位置,$L=3$.求这两个后缀的$\text{lcp}$,得到的是圆括号和方括号的区间,长度为$len=7$.那么重复次数为$\lfloor\frac{len}L\rfloor+1=3$.然而这两个子串还可能向前延展(花括号部分),这时候至少要延展$L-len\text{ mod }L$的长度才可能使重复次数增大,所以只要找到那个位置再求$\text{lcp}$判一下就行了.那如果再向前延展呢?这种情况一定已经被之前的枚举考虑过了.也可以这么理解,我们此时枚举的是不经过$str[(k-1)L]$的子串.

到这里的复杂度是稳稳的$O(n\ln n)$.

但是我们还要求字典序最小的解...这个嘛...暂时不知道有什么好的处理方法,一种略暴力的做法是存下能使答案最大的所有$L$,然后按字典序从小到大枚举来判.感觉应该可以被卡掉?

哪位大神若有不暴力的方法麻烦告诉本蒟蒻...

补:其实有不暴力的方法求字典序最小解,在求解答案的同时还需要往前面的一段区间内再用一个ST表找$rk$值最小的,这样就能保证复杂度是$O(n\ln n)$,有些麻烦,暂时没打算写.

### 代码

```c++
#include <cstdio>
#include <cmath>
#include <ctime>
#include <cctype>
#include <cstring>
#include <cstdlib>
#include <cassert>
#include <set>
#include <map>
#include <queue>
#include <vector>
#include <bitset>
#include <complex>
#include <iostream>
#include <algorithm>
#define fi first
#define se second
#define pb push_back
#define y1 kjfasiv
#define lowbit(x) (x&-x)
#define debug(x) cout<<#x<<"="<<x<<endl
#pragma comment(linker, "/STACK:1024000000,1024000000")
using namespace std;
typedef long long ll;
typedef unsigned long long ull;
typedef unsigned int uint;
typedef pair<int,int> pii;
typedef pair<ll,ll> pll;
typedef vector<int> veci;
typedef complex<double> Com;
const int mod=(int)1e9+7,inf=0x7fffffff,rx[]={-1,0,1,0},ry[]={0,1,0,-1};
const ll INF=1ll<<60;
const double pi=acos(-1.0),eps=1e-8;
template<class T>void rd(T &res){
    res=0;
    char c;
    while(c=getchar(),c<48);
    do res=res*10+(c^48);
        while(c=getchar(),c>47);
}
template<class T>void rec_print(T x){
    if(!x)return;
    rec_print(x/10);
    putchar(x%10^48);
}
template<class T>void print(T x){
    if(!x)putchar('0');
    else rec_print(x);
}
template<class T>inline void Max(T &a,T b){
    if(b>a)a=b;
}
template<class T>inline void Min(T &a,T b){
    if(b<a)a=b;
}
inline void mod_add(int &a,int b){
    if((a+=b)>=mod)a-=mod;
}
int fast_mod_pow(int a,int b){
    int res=1;
    for(;b;b>>=1,a=1ll*a*a%mod)
        if(b&1)res=1ll*res*a%mod;
    return res;
}
const int N=(int)1e5+5,LG=18;
char str[N];
int lg[N];
void init(){
    for(int i=2,j=1;i<N;++i){
        lg[i]=j;
        if(!(i&(i-1)))++j;
    }
}
struct Suffix_Array{
    int n,sa[N],rk[N],height[N],cnt[N],
        ST[LG][N],arr[N];
    void construct(){
        int m='z'+1,*a=rk,*b=height;
        str[(n=strlen(str))++]='#';
        memset(cnt,0,m<<2);
        for(int i=0;i<n;++i)++cnt[a[i]=str[i]];
        for(int i=1;i<m;++i)cnt[i]+=cnt[i-1];
        for(int i=n-1;~i;--i)sa[--cnt[a[i]]]=i;
        for(int j=1,p=0;p<n;j<<=1,m=p){
            p=0;
            for(int i=n-j;i<n;++i)b[p++]=i;
            for(int i=0;i<n;++i)if(sa[i]>=j)b[p++]=sa[i]-j;
            memset(cnt,0,m<<2);
            for(int i=0;i<n;++i)++cnt[a[b[i]]];
            for(int i=1;i<m;++i)cnt[i]+=cnt[i-1];
            for(int i=n-1;~i;--i)sa[--cnt[a[b[i]]]]=b[i];
            swap(a,b);
            p=1;
            a[sa[0]]=0;
            for(int i=1;i<n;++i)
                a[sa[i]]=b[sa[i-1]]==b[sa[i]]&&b[sa[i-1]+j]==b[sa[i]+j]?p-1:p++;
        }
        --n;
        for(int i=1;i<=n;++i)
            rk[sa[i]]=i;
        for(int i=0,h=0;i<n;height[rk[i++]]=h){
            if(h)--h;
            for(int j=sa[rk[i]-1];str[i+h]==str[j+h];++h);
        }
    }
    void ST_init(){
        for(int i=2;i<=n;++i)
            ST[0][i]=height[i];
        for(int j=1;1<<j<n;++j)
            for(int i=2;i+(1<<j)-1<=n;++i)
                ST[j][i]=min(ST[j-1][i],ST[j-1][i+(1<<j-1)]);
    }
    int query_mi(int L,int R){
        int k=lg[R-L+2]-1;
        return min(ST[k][L],ST[k][R-(1<<k)+1]);
    }
    int lcp(int a,int b){
        if(rk[a]>rk[b])swap(a,b);
        return query_mi(rk[a]+1,rk[b]);
    }
    void solve(){
        ST_init();
        int ans=0,tot=0;
        for(int L=1;L<=n>>1;++L)
            for(int i=0,j=L;j<n;i=j,j+=L){
                int len=lcp(i,j),
                    res=len/L+1;
                len=L-len%L;
                int pre=i-len;
                if(pre>=0&&lcp(pre,pre+L)>=len)++res;
                if(res>ans){
                    ans=res;
                    arr[(tot=0)++]=L;
                }
                else if(res==ans&&arr[tot-1]!=L)arr[tot++]=L;
            }
        if(ans==1){
            putchar(str[sa[1]]);
            putchar('\n');
            return;
        }
        for(int i=1;;++i){
            for(int j=0;j<tot;++j){
                int tmp=sa[i]+arr[j];
                if(tmp<n&&lcp(sa[i],tmp)>=arr[j]*(ans-1)){
                    for(int k=sa[i],en=sa[i]+arr[j]*ans;k<en;++k)
                        putchar(str[k]);
                    putchar('\n');
                    return;
                }
            }
        }
    }
}SA;
int main(){
    init();
    int kase=0;
    while(~scanf("%s",str)&&str[0]!='#'){
        printf("Case %d: ",++kase);
        SA.construct();
        SA.solve();
    }
    return 0;
}
/*
    
    Jul.27.16
    
    Tags:SA
    Submissions:1
    
    Memory 8940K
    Time 313MS
    Code Length 3579B
    
*/
```

## POJ3415 Common Substrings

[**题目链接**](http://poj.org/problem?id=3415)

### 题目大意

求两个只包含英文字母的字符串的长度大于等于$K$的公共子串的个数.位置不同的子串就视为不同的子串.

字符串长度$n,m\le10^5,1\le K\le\min(n,m).$

### 题解

先上两个基本套路,即拼接字符串和$height$数组分组.

然后我们对于$sa[j]>n$,需要求:

$$
\sum\limits_{\substack{i<j,\\sa[i]<n}}(\min\limits_{i<k\le j}\{height[k]\}-K+1).
$$

其中还需满足$i$与$j$在同一分组内.

对于$sa[j]<n$的情况也要用同样的方法算一遍$sa[i]>n$的和.

为了高效求这个东西,我们需要维护一个$height$值单调递增的单调栈,记录栈中每个元素的$height$值以及对应的$i$的个数,这样可以做到在$O(n)$的时间复杂度内实时维护那个和式.

于是构造SA的复杂度是$O(n\lg n)$,求解的复杂度是$O(n)$.

个人感觉实现起来有点小麻烦...

### 代码

```c++
#include <cstdio>
#include <cmath>
#include <ctime>
#include <cctype>
#include <cstring>
#include <cstdlib>
#include <cassert>
#include <set>
#include <map>
#include <queue>
#include <vector>
#include <bitset>
#include <complex>
#include <iostream>
#include <algorithm>
#define fi first
#define se second
#define pb push_back
#define y1 kjfasiv
#define lowbit(x) (x&-x)
#define debug(x) cout<<#x<<"="<<x<<endl
#pragma comment(linker, "/STACK:1024000000,1024000000")
using namespace std;
typedef long long ll;
typedef unsigned long long ull;
typedef unsigned int uint;
typedef pair<int,int> pii;
typedef pair<ll,ll> pll;
typedef vector<int> veci;
typedef complex<double> Com;
const int mod=(int)1e9+7,inf=0x7fffffff,rx[]={-1,0,1,0},ry[]={0,1,0,-1};
const ll INF=1ll<<60;
const double pi=acos(-1.0),eps=1e-8;
template<class T>void rd(T &res){
    res=0;
    char c;
    while(c=getchar(),c<48);
    do res=res*10+(c^48);
        while(c=getchar(),c>47);
}
template<class T>void rec_print(T x){
    if(!x)return;
    rec_print(x/10);
    putchar(x%10^48);
}
template<class T>void print(T x){
    if(!x)putchar('0');
    else rec_print(x);
}
template<class T>inline void Max(T &a,T b){
    if(b>a)a=b;
}
template<class T>inline void Min(T &a,T b){
    if(b<a)a=b;
}
inline void mod_add(int &a,int b){
    if((a+=b)>=mod)a-=mod;
}
int fast_mod_pow(int a,int b){
    int res=1;
    for(;b;b>>=1,a=1ll*a*a%mod)
        if(b&1)res=1ll*res*a%mod;
    return res;
}
const int N=(int)2e5+5;
int K;
char str[N];
struct Suffix_Array{
    int n,sa[N],rk[N],height[N],cnt[N];
    pii stk[N];
    void construct(){
        int m='z'+1,*a=rk,*b=height;
        str[(n=strlen(str))++]='#';
        memset(cnt,0,m<<2);
        for(int i=0;i<n;++i)++cnt[a[i]=str[i]];
        for(int i=1;i<m;++i)cnt[i]+=cnt[i-1];
        for(int i=n-1;~i;--i)sa[--cnt[a[i]]]=i;
        for(int j=1,p=0;p<n;j<<=1,m=p){
            p=0;
            for(int i=n-j;i<n;++i)b[p++]=i;
            for(int i=0;i<n;++i)if(sa[i]>=j)b[p++]=sa[i]-j;
            memset(cnt,0,m<<2);
            for(int i=0;i<n;++i)++cnt[a[b[i]]];
            for(int i=1;i<m;++i)cnt[i]+=cnt[i-1];
            for(int i=n-1;~i;--i)sa[--cnt[a[b[i]]]]=b[i];
            swap(a,b);
            p=1;
            a[sa[0]]=0;
            for(int i=1;i<n;++i)
                a[sa[i]]=b[sa[i-1]]==b[sa[i]]&&b[sa[i-1]+j]==b[sa[i]+j]?p-1:p++;
        }
        --n;
        for(int i=1;i<=n;++i)
            rk[sa[i]]=i;
        for(int i=0,h=0;i<n;height[rk[i++]]=h){
            if(h)--h;
            for(int j=sa[rk[i]-1];str[i+h]==str[j+h];++h);
        }
        // puts(str);//
        // for(int i=1;i<=n;++i){
        //  for(int j=sa[i];j<n;++j)
        //      putchar(str[j]);//
        //  putchar('\n');//
        //  debug(height[i]);//
        // }
    }
    void solve(int m){
        ll ans=0,sum=0;
        for(int i=2,top=0;i<=n;++i){
            if(height[i]<K)sum=top=0;
            else{
                int cnt=0;
                if(sa[i-1]<m){
                    ++cnt;
                    sum+=height[i]-K+1;
                }
                while(top&&stk[top-1].fi>=height[i]){
                    --top;
                    cnt+=stk[top].se;
                    sum-=stk[top].se*(stk[top].fi-height[i]);
                }
                if(cnt)stk[top++]=pii(height[i],cnt);
                if(sa[i]>m)ans+=sum;
            }
        }
        sum=0;
        for(int i=2,top=0;i<=n;++i){
            if(height[i]<K)sum=top=0;
            else{
                int cnt=0;
                if(sa[i-1]>m){
                    ++cnt;
                    sum+=height[i]-K+1;
                }
                while(top&&stk[top-1].fi>=height[i]){
                    --top;
                    cnt+=stk[top].se;
                    sum-=stk[top].se*(stk[top].fi-height[i]);
                }
                if(cnt)stk[top++]=pii(height[i],cnt);
                if(sa[i]<m)ans+=sum;
            }
        }
        print(ans);
        putchar('\n');
    }
}SA;
int main(){
    while(rd(K),K){
        scanf("%s",str);
        int m=strlen(str);
        str[m]='$';
        scanf("%s",str+m+1);
        SA.construct();
        SA.solve(m);
    }
    return 0;
}
/*
    
    Jul.28.16

    Tags:SA,stack
    Submissions:1

    Memory 5568K
    Time 782MS
    Code Length 3417B

*/

```

# 总结

呃呃...写了这么多后缀数组的学习也差不多告一段落啦.

总结起来说,后缀数组的用法说灵活也灵活,说死板也死板;现在的我只是写了几道入门的经典题目,更多的还是需要日后做题的积累啊.

愉快地去补多校了~~

~~所以到头来相当于什么也没有总结~~
