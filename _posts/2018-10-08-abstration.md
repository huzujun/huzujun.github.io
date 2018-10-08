---
layout: post
title: "abstration"
date: 2018-10-08 19:17:06
image: '/assets/img/'
description: java面向对象思想剩下的最重点，一网打尽。
tags: 
- java
categories:
- language
twitter_text: 


## 多态

这里抛出一个观点，之前学的继承，还有这篇文章要讲的抽象和接口，都是为了实现多态（polymorphic）而服务的。

这里举个继承的例子

```java
public class Animal{
    String name;
    void breath(){
        System.out.println("Breath with nose and mouth");
    }
}
public class dog extend Animal {}
public class cat extend Animal {}
```

这里定义了一个父类 Animal，以及它的子类dog和cat

因此我们可以很方便的**向上转型**，比如这样（new 一个 dog，但是类型是Animal，即向上改变类类型）：

```java
Animal a = new dog();
a.breath();
```

## 抽象 （abstract）

上面的程序编译上没有问题，但是我们如果想再加一个子类，比如鱼 (Fish)，然后我们发现它不是用嘴巴和鼻子呼吸的，breath 方法写得不太妥

那该怎么办呢？

一个显而易见的解决方案是把 breath 方法转移到每个具体的动物类里面，然后把 Animal 类里面的breath方法删掉，那么就对每个动物都有不一样的breath方法了。

但是这样会有个问题！我们不能实现 breath 方法的**多态**了！也就是说：

```java
dog a = new dog();
a.breath();
```

是没问题的，但是

```java
Animal a = new dog();
a.breath();
```

是不能编译通过的，因为父类 Animal 没有这个 breath 方法。

这是候我们的**抽象**可以登场了，我们要把 breath 写成抽象的方法。

要注意，**要定义抽象的方法，类也要定义成抽象的，但是抽象的类不一定只有抽象的方法，也可以有具体的方法**。

比如我们这样改写一下我们的程序

```java
abstract class Animal{
    abstract void breath();
}
public class Cat extends Animal {
    void breath(){
        System.out.println("Breath with nose and mouth"); 
    }
}
public class Fish extends Animal {
    void breath(){
        System.out.println("Breath with gill"); 
    }
}
```

父类 Animal 里声明了 breath 方法，但是并不实现它，具体的实现在子类中，这样我们既实现了 Animal 类 breath 方法的多态，也保证了不同动物有不一样的呼吸方法。

## 接口（interface）

现在我们想定义一个 PlayWithOwner （和主人撒娇）的方法，那么问题来了...猫和狗撒娇还说的过去，如果我们想定义一个狮子类，那可能就不能撒娇了，（有损它的尊严）。

一个简单的想法是定义一个 Pet（宠物）类，但是 java 规定一个类不能继承两个父类，即　dog 类不能同时继承 Animal 又继承 Pet

但是我们有接口，接口这样定义：

```java
abstract class Animal{
    abstract void breath();
}
public interface Pet {
    void PlayWithOwner();
}
public class Dog extends Animal implements Pet {
    void breath(){
        System.out.println("Breath with nose and mouth"); 
    }
    public void PlayWithOwner(){
        //balabala
    }
}
public class Lion extends Animal {
    void breath(){
        System.out.println("Breath with nose and mouth"); 
    }
}
```

和抽象方法类似，接口里面只声明方法的定义

```java
Pet a = new dog();
a.PlayWithOwner();
```

另外，继承只能继承一个，但是接口能接很多个。

## 向下转型与向上转型

有向上转型，就有向下转型。

- **向上转型**的好处简单来说就是：无论你是什么子类，只要你继承了父类，都可以把所有子类的对象统一转为父类的对象，然后执行父类的方法，**但是这么做会丢失子类本身的方法**
- **向下转型**，把向上转型后的对象再转变回来。对象变成父类后，虽然可以方便地统一调用父类的方法，但是自己**本身扩展的方法不能用了**，这时只要转回来就行了

具体怎么实现以及多态的好处在下面这段程序中一展无遗：

```java
public static void show(Animal a)  {
   a.eat();  
   // instanceof　是类型判断，向上转型后仍然保存着原来是什么类型
   if (a instanceof Cat)  {  // 猫做的事情 
            Cat c = (Cat)a;  //向上转型
            c.work();  
   } else if (a instanceof Dog) { // 狗做的事情 
            Dog c = (Dog)a;  //向上转型
            c.work();  
   }  
}  
```

## 总结

抽象方法和接口的意义在于制定一种规范，比如规范狗、猫的撒娇方法，调用格式都一样，使得多态可以实现。

---
