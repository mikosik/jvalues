
Starting with jValues is easy, just grab jar file from http://code.google.com/p/jvalues/downloads/list and add it to your project. You need to import all methods from one class to start with jValues:

```java
import static com.perunlabs.common.vars.V.*;
```

**Basic types (primitive wrappers)**

Now let's see some examples of creating objects that will represent simple float values (We focus on float at beginning but other types are present as well even compound like vector or quad, more about it later):

```java
FloatC pi = floatC(3.14f);
FloatV radius = floatV(2);
```

We've just created const (immutable) `pi` value and `radius` variable (mutable). Both objects implement `FloatG` interface which has just one method returning `float`. This way method can accept `FloatG` type when they don't care whether they arguments are mutable or not. Methods can also force callers to use const types when immutability is needed (for example in case of concurrent code). Let me just print this interface here to make everything clear.

```java
public interface FloatG {
  public float get();
}
```

Seems trivial so far but wait a little bit and we reach some magic soon. For now let's see normal way to perform operation on `FloatV` variables. We have already created `radius` and stored 2 in it. We can simply multiply it by 3 this way:

```java
radius.mul(2);
```

`FloatV` supports most common operations you would require from `floats`. Now all of these give nothing more than normal primitive java values. Let' make use the fact that we are dealing with real objects. The `V` class from which we imported all static methods at the beginning comes with set of methods that allow us to create just-in-time calculated values. Let's calculate perimeter of a circle using well known formula p = 2 \pi r\,. It should go like this (we are reusing `pi` and `radius` objects declared a few lines above):

```java
FloatG perimeter = mul(radius, mul(pi, floatC(2)));
System.out.println(perimeter) // prints 12.56
```

Now we can change the value of `radius`

```java
radius.set(10);
```

and `perimeter` value object will be updated automatically.

```java
System.out.println(perimeter) // prints 62.8
```

As can be seen values compound this way retain references to the whole calculation and are always up to date. We created object that implements simplest possible interface (`FloatG`) and keeps (hides) inside complicated calculations. This `FloatG` interface (and analogical XxxG interfaces for each primitive java type and also compound jValues) is a core of the library - it allows you to have components in your game that can be easily joined together by exchanging information via this interface. For example if you have component that draws progress bar - just make it accept `FloatG` in its constructor as a source of progress state. Now you can instantiate such progress bar giving it anything that implements `FloatG`. It can be either 'time to checkpoint' that will countdown during game level, or 'hero health' that will show current status of our hero. This simple interface removes the need of creating Model objects known from other frameworks (e.g. Java Swing). This interface is a model by itself.
Note that such design is possible because UI (and whole graphic) in game are usually drawn 60 times per second without checking whether something was changed in UI/game or not. This is due to the fact that games are mostly very dynamic things and most objects to be drawn changes quickly. In standard UIs (like Swing) you want to update (redraw) part of UI only when it actually changes that's why such frameworks are filled with onUpdate(), onRedraw() and other similar methods.

If you worry about performance let me mention that static methods from `V` class are smart enough to avoid just-in-time calculation when all jValues used as arguments in calculation are const (immutable) and result can be calculated once. Such cases are detected and `FloatC` value is returned so invoking `get()` each time does not perform any additional computations. For example multiplying two const values will return const value calculated once during method call:

```java
FloatC rectangleArea = mul(floatC(3), floatC(4));
```

This call actually invoked overloaded  

```java
public static FloatC V.mul(FloatC, FloatC)
```

function (takes two `FloatC`) arguments and returns `FloatC` but even when version with two `FloatG` is invoked:

```java
public static FloatG V.mul(FloatG, FloatG)
```

it internally detects that arguments are instances of `FloatC`, will calculate result once, and return instance of `FloatC`.


**Runnable interface**

Another interface that is a core in UI programming is Action interface. You probably seen something similar if you programmed UIs (especially in java Swing). It usually forces you to create lots of anonymous inner classes that just tells some UI component what should be invoked when user triggers some action. With jValues you can simplify most of such code. `FloatV` method always come in pairs. For example method `add(FloatG)` has companion method `addR(FloatG)`:

```java
public interface FloatV ... {

  public FloatV add(FloatG addend);

  public Runnable addR(FloatG addend); 

  [...] 
} 
```

The companion method (one with R suffix in its name) that will invoke its companion `add(...)` method each time its "run" method is called. If you need a button that increases speed of car in your game each time it is pressed do this:

```java
FloatV speed = floatV(0);
FloatG speedIncrease = floatC(0.5f); 
Button button = new Button(speed.addR(speedIncrease));
```

Each time button is pressed it will simply run given `Runnable` which will execute appropriate action. Note that `speedIncrease` object does not have to be plain value but just-in-time object itself. Let's see how we can extend our game by adding afterburner to our car so once it is enabled `speedIncrease` will be higher with each button press:

```java
FloatC normalSpeedIncr = floatC(10);
FloatC afterburnerSpeedInc = floatC(30);
BoolV afterburnerOn = boolV(false);
FloatG speedIncrease = iff(afterburnerOn, afterburnerSpeedIncr, normalSpeedIncr);
```

Note we introduced `iff` method from `V` api. As can be guessed it simply returns value from second or third parameter depending on boolean condition value from first parameter. Now turning on `afterburner` from code is as simple as:

```java
afterburnerOn.set(true);
```

Note that `afterBurnerOn` is jValue itself so we can manipulate its value from UI. Creating UI button that would allow user switching afterburner on/of requires this code (method `not()` negates the current value in `BoolV` object):

```java
new Button(afterburnerOn.notR());
```

If you want allow user only turning on `afterburner` (without possibility to turn it off) use this line:

```java
new Button(afterburnerOn.setR(true));
```

it will set `afterBurnerOn` to true each time button is pressed.


**Building your objects**

Just to make sure you got the whole mental shift we covered here let's just show how jValues can be used for building your own objects. As we already described UI framework that does not need primitive values, Action interfaces, anonymous classes and other bloat, now we focus on your own classes and show why you don't need primitive types anymore. Let's design simple class Hero that will represent your game hero fighting opponents in your game:

```java
public class Hero {
  private static final FloatC INITIAL_HEALTH = floatC(100); 
  private FloatV health = floatV(INITIAL_HEALTH);
  // isGt method means "is Greater Than" 
  private BoolG isAlive = isGt(health, 0);

  public FloatG health() {
    return health; 
  }

  public BoolG isAlive() {
    return isAlive;
  }

  public void takeDamage(FloatG damage) {
    health.sub(damage); 
  }

  public void eatMedicine() {
    health.set(INITIAL_HEALTH); 
  }
}
```

Note that: There is not a single old-school getter/setter method in this code. Methods takes/returns jValues objects. We have `health()` and `isAlive()` methods that returns XxxG interfaces that are read-only - we encapsulate our Hero state this way. If we need give more access to code that calls Hero we can simply return `FloatV` in case of `health()` method - this would be equal to creating setter in old-school code but we would achieve that with only one method.


**Compound Types**

Once we covered basic types let's jump to some advanced values. Our float jValues can be easily compound into another types. As you suspect everything that was true for float jValues is true for vector jValues:

```java
VectorC earthGravity = vectorC(0, -10);
FloatV ballMass = floatV(1);
VectorG force = mul(earthGravity, ballMass);

System.out.println(force) // prints "vector(0, -10)"
ballMass.set(2);
System.out.println(force) // prints "vector(0, -20)"
```

Composition is not limited to one level only. For example `Quad` (quadrilateral) is composition of four vectors, each containing position of one quad vertex:

```java
QuadC rectangle = quad(vectorC(0, 0), vectorC(1, 0), vectorC(1, 1), vectorC(0, 1));
FloatV angle = floatV(0);
QuadG rotated = rotate(rectangle, angle);
```

**Future improvements**

jValues is pretty young library so might miss some functionality that you find important in your project. If that is the case drop me a line. Whole library code is autogenerated by java tool created for that purpose so adding new types and functions is quite painless.
