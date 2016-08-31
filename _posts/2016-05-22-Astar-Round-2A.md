---
layout: post
title: "Astar Round 2A"
date: 2016-05-22 21:38:00
description: 'Solution for Astar Round 2A.'
tags:
- Astar
categories:
- Contest
---



这一场比得不算太好吧...主要是机房里人太多了,对面初三的只会写第一题一直在各种bb....略不爽..
网上题解标程什么的似乎也有了,于是这里按照我做题的顺序略微写几句吧.
[**题目链接**](http://acm.hdu.edu.cn/search.php?field=problem&key=2016%22%B0%D9%B6%C8%D6%AE%D0%C7%22+-+%B3%F5%C8%FC%A3%A8Astar+Round2A%A3%A9&source=1&searchmode=source)
## 1001 All X ##
 细节比较多的一道模拟题..基本思路很简单,就是找到$m$个$x$模$k$的循环节然后计算即可.
复杂度$O(k)$.看很多大神也WA了不少次很欣慰...
## 1006 Gym Class ##
$A$同学不希望$B$同学排在他之前可以转化为$A$同学必须排在$B$同学前面,从$A$向$B$连边,然后贪心选编号大的拓扑排序即可.
复杂度$O(n\lg n+m)$.
## 1002 Sitting in Line ##
看到$n\le16$除了状压之外没有别的选择了.
一开始最简单的想法就是三维的dp状态:$dp[i][j][S]$表示排了$i$个人,最后一个人为$j$,前面排过的人集合为$S$.然后有简单的$O(n)$转移,于是复杂度$O(n^3\cdot2^n)$.当然会T啦...
这时候重新考虑一下状态就可以发现,"排了几个人"这个状态是可以由$S$知道的...于是状态数降为$O(n\cdot2^n)$.按$S$从小到大继续写简单的$O(n)$转移的前推dp就能轻松过了.总复杂度$O(n^2\cdot2^n)$.

```c++
#include<cstdio>
#include<cstring>
#include<algorithm>
using namespace std;
const int N=20,MAX_S=1<<16,NEG_INF=-2139062144;
int n,val[N],lim[N],id[N],bitcnt[MAX_S+5],dp[N][MAX_S+5];
void init(){
    bitcnt[0]=0;
    for(int i=1;i<MAX_S;++i)
        bitcnt[i]=bitcnt[i>>1]+(i&1);
}
inline void Max(int &a,int b){
    if(b>a)a=b;
}
void solve(){
    scanf("%d",&n);
    for(int i=0;i<n;++i)
        id[i]=-1;
    for(int i=0;i<n;++i){
        scanf("%d%d",&val[i],&lim[i]);
        if(~lim[i])id[lim[i]]=i;
    }
    if(n==1){
        puts("0");
        return;
    }
    int ans=NEG_INF;
    memset(dp,128,sizeof(dp));
    int mx_S=(1<<n)-1;
    if(id[0]==-1){
        for(int i=0;i<n;++i)
            dp[i][1<<i]=0;
    }
    else dp[id[0]][1<<id[0]]=0;
    for(int S=0;S<mx_S;++S)
        for(int i=0;i<n;++i){
            if(dp[i][S]==NEG_INF)continue;
            int cnt=bitcnt[S];
            if(id[cnt]==-1){
                for(int j=0;j<n;++j){
                    if(S&1<<j)continue;
                    Max(dp[j][S|1<<j],dp[i][S]+val[i]*val[j]);
                }
            }
            else{
                Max(dp[id[cnt]][S|1<<id[cnt]],dp[i][S]+val[i]*val[id[cnt]]);
            }
        }
    for(int i=0;i<n;++i){
        Max(ans,dp[i][mx_S]);
    }
    printf("%d\n",ans);
}
int main(){
    int cas,kase=0;
    scanf("%d",&cas);
    init();
    while(cas--){
        printf("Case #%d:\n",++kase);
        solve();
    }
    return 0;
}
```
或许是状压还练得不够,这题花了好多时间TAT...
然后下一题比较杯具地就卡死了....
## 1005 BD String ##
这题比较明显的是那个$2^{1000}$没有什么卵用...因为L,R的范围是$10^{18}$,翻个大概60多次$S$就会超过这个长度,后面就没用了.
然后这题其实就没有了...
把问题转化为求$[1,n]$中$B$的个数,找到长度小于等于它的最后一个$S$,这个$S$中$B$的个数可以直接计算出来,后面的"翻"到前面去递归求解就行了.
思路很简单不是么?其实实现起来也不难,但是我犯了一个略奇葩的罕见错误,比赛结束了才调出来QAQ...
我写了一个```1<<k```,这里的```k```是```long long```类型,然而1并不是```long long```,然后就炸了-.-左移号右边是```long long```没用啊...一定要```1ll<<k```...
##1003 Snacks##
比赛时不写这题简直是失败啊!维护每个子树中$sum[i]$的最大值即可($sum[i]$表示$i$到根节点的点权和).裸题啊!比赛时抓一点模板10几分钟应该就能A了...然而却看都没看...QAQ
随手放一下代码好了.

```c++
#pragma comment(linker, "/STACK:1024000000,1024000000")
#include<cstdio>
#include<cstring>
#include<algorithm>
using namespace std;
const int N=1e5+5;
int dfs_clock,tot_edge,head[N],pre[N],post[N],val[N];
typedef __int64 ll;
const ll INF=1ll<<60;
struct Edge{
    int to,nxt;
}edge[N<<1];
struct Segment_Tree{
    struct Node{
        int L,R;
        ll mx,extra;
    }tree[N<<2];
    inline void push_up(int k){
        tree[k].mx=max(tree[k<<1].mx,tree[k<<1|1].mx);
    }
    void push_down(int k){
        ll &tmp=tree[k].extra;
        if(!tmp)return;
        tree[k<<1].mx+=tmp;
        tree[k<<1|1].mx+=tmp;
        tree[k<<1].extra+=tmp;
        tree[k<<1|1].extra+=tmp;
        tmp=0;
    }
    void init(int L,int R,int k=1){
        tree[k].L=L;
        tree[k].R=R;
        tree[k].mx=-INF;
        tree[k].extra=0;
        if(L==R)return;
        int mid=L+R>>1;
        init(L,mid,k<<1);
        init(mid+1,R,k<<1|1);
    }
    void assign(int tar,ll amt,int k=1){
        if(tree[k].L==tree[k].R){
            tree[k].mx=amt;
            return;
        }
        push_down(k);
        int mid=tree[k].L+tree[k].R>>1;
        if(tar<=mid)assign(tar,amt,k<<1);
        else assign(tar,amt,k<<1|1);
        push_up(k);
    }
    ll query(int L,int R,int k=1){
        if(tree[k].L==L&&tree[k].R==R)return tree[k].mx;
        push_down(k);
        int mid=tree[k].L+tree[k].R>>1;
        if(R<=mid)return query(L,R,k<<1);
        if(L>mid)return query(L,R,k<<1|1);
        return max(query(L,mid,k<<1),query(mid+1,R,k<<1|1));
    }
    void add(int L,int R,int amt,int k=1){
        if(tree[k].L==L&&tree[k].R==R){
            tree[k].mx+=amt;
            tree[k].extra+=amt;
            return;
        }
        push_down(k);
        int mid=tree[k].L+tree[k].R>>1;
        if(R<=mid)add(L,R,amt,k<<1);
        else if(L>mid)add(L,R,amt,k<<1|1);
        else{
            add(L,mid,amt,k<<1);
            add(mid+1,R,amt,k<<1|1);
        }
        push_up(k);
    }
}T;
inline void add_edge(int from,int to){
    edge[tot_edge].to=to;
    edge[tot_edge].nxt=head[from];
    head[from]=tot_edge++;
}
void dfs(int cur,int par,ll sum){
    T.assign(pre[cur]=++dfs_clock,sum+=val[cur]);
    for(int i=head[cur];~i;i=edge[i].nxt){
        int to=edge[i].to;
        if(to==par)continue;
        dfs(to,cur,sum);
    }
    post[cur]=dfs_clock;
}
void solve(){
    int n,m,ope,x,y;
    scanf("%d%d",&n,&m);
    tot_edge=0;
    memset(head,-1,n<<2);
    for(int i=1,u,v;i<n;++i){
        scanf("%d%d",&u,&v);
        add_edge(u,v);
        add_edge(v,u);
    }
    for(int i=0;i<n;++i)
        scanf("%d",&val[i]);
    T.init(1,n);
    dfs_clock=0;
    dfs(0,-1,0);
    while(m--&&scanf("%d%d",&ope,&x)){
        if(ope)printf("%I64d\n",T.query(pre[x],post[x]));
        else{
            scanf("%d",&y);
            T.add(pre[x],post[x],y-val[x]);
            val[x]=y;
        }
    }
}
int main(){
    int cas,kase=0;
    scanf("%d",&cas);
    while(cas--){
        printf("Case #%d:\n",++kase);
        solve();
    }
    return 0;
}
```
##1004 D Game##
这题是最难的一题了...比赛时最后只过了20个人吧好像.
比赛那一天晚上几乎都在想这题.唔...看着像区间dp?看数据范围目测复杂度$O(n^3)$或$O(n^3\lg n)$?状态怎么定义?$dp[L][R]$是肯定要的吧...然后$O(n)$转移?再加一维状态然后$O(1)$转移?反正都不会....
想了一会就把三维的状态排除掉了...二维状态又能表示一些什么呢?
仔细思考一下,这题的难点就是消除一段区间时可以先把中间的几小段删掉,然后剩下的合并成等差数列再消掉.而且一段区间也不一定能消得完.
这时ShinFeb给了我信心,认为可以用$dp[L][R$]表示$[L,R]$能否消完...我之前一直不敢这么定义,因为用dp存一个```bool```一直是很浪费的不是么?(于是ShinFeb说改成```char```就不那么浪费了=.=)
这样的话要求最终答案还得来一个$O(n^2)$的小dp.但这都好说,关键是怎么转移啊?转移时再来一层dp?
没错,ShinFeb就这样写了个玄学的"三重dp"$O(n^3)$过掉了...膜拜TAT..
扯的有点多了...后来看了所谓的题解第一句话就秒懂了...
>每个等差数列都可以拆成若干长度为2或3的等差数列.

于是这题成功变成了一道极水的区间dp...复杂度大概$O(n^3\lg n)$吧...

```c++
#include<cstdio>
#include<algorithm>
using namespace std;
const int N=305;
int m,num[N],delta[N];
bool dp[N][N];
int DP[N];
inline void Max(int &a,int b){
    if(b>a)a=b;
}
bool exist(int tar){
    int k=lower_bound(delta,delta+m,tar)-delta;
    return k<m&&delta[k]==tar;
}
void solve(){
    int n;
    scanf("%d%d",&n,&m);
    for(int i=1;i<=n;++i){
        scanf("%d",&num[i]);
        dp[i][i-1]=true;
    }
    for(int i=0;i<m;++i)
        scanf("%d",&delta[i]);
    sort(delta,delta+m);
    for(int d=2;d<=n;++d){
        for(int i=1,j=i+d-1;j<=n;++i,++j){
            dp[i][j]=false;
            if(dp[i+1][j-1]&&exist(num[j]-num[i])){
                dp[i][j]=true;
                continue;
            }
            for(int k=i+1;k<j-1;++k){
                if(dp[i][k]&&dp[k+1][j]){
                    dp[i][j]=true;
                    break;
                }
            }
            if(!dp[i][j]){
                for(int k=i+1;k<j;++k){
                    if(num[k]-num[i]==num[j]-num[k]&&dp[i+1][k-1]&&dp[k+1][j-1]&&exist(num[k]-num[i])){
                        dp[i][j]=true;
                        break;
                    }
                }
            }
        }
    }
    DP[0]=0;
    for(int i=1;i<=n;++i){
        DP[i]=DP[i-1];
        for(int j=1;j<i;++j){
            if(!dp[j][i])continue;
            Max(DP[i],DP[j-1]+i-j+1);
        }
    }
    printf("%d\n",DP[n]);
}
int main(){
    int cas;
    scanf("%d",&cas);
    while(cas--)solve();
    return 0;
}
```
这场比赛其实状态好应该可以解5题....不说了说多了都是泪.
还是在线赛经验不足的缘故吧,有时候大脑会不在状态.
怎么说还是稳进了复赛并涨到了紫名(炒鸡感动).
~~预感到以后再也不敢比BC常规赛了....~~

