jValues

Explanation of concepts/naming used in the whole jValues generator code.
Basic knowledge of jValue library usage/API is advised before reading this.

--- jValues level level concepts: ---

jValue - high level concept representing a value which can be represented/manifested/accessed
in java code under/through various interfaces/classes. jValue can have different forms: immutable,
mutable, read-only (mutable - client cannot change it but has to be aware that
it can be changed under the mask)

VType - jValue type - keeps information about jValue type meaning which primitive java type
it maps to (in case of basic VType) or of which JValues it is built of (in case of Compound VType). 
 
Basic VType - VType that represent most basic (indivisible value). It always maps to some
java primitive type. For example VFloat VType maps to java float.

Compound VType - VType that represents compound VType which is built of other VTypes known
as its vComponents. For example Vector is built of two VFloat components named "x" and "y". 

VFunction - operation that can be performed on some VTypes (its parameters) and gives VType result.
Each VFunction is associated with exactly one VType. This way appropriate java methods is
generated in owner's XxxV interface (apart from methods in "V" class api).

VComponent - Describes vComponent of VType. Basic VType has exactly one PrimitiveVComponent.
Compound VType has one or more ObjectiveVComponent. 

PrimitiveVComponet - part of each Basic VType. It holds primitive JType to which VType enclosing
this PrimitiveVComponent maps.

ObjectiveVComponent - part of Compound VType. Maps to some VType.

--- java level concepts ---

JType - java type = class or interface or primitive type

Each VType is used to generate a set of JTypes representing given jValue in java code.
For example VFloat is used to generate FloatG, FloatS, FloatV, FloatC, AbstractFloatG,
AbstractFloatV, FloatVImpl. Each such JType is represented by appropriate value in
VTypeKind enum. Generator code can ask VType for JType to be generated this way:
VType vType = AllVTypes.VFLOAT;
JType  floatG = vType.jType(VTypeKind.XxxG);
VTypeKind has also two special values: MostPrimitiveG, MostPrimitiveC. When these
values are passed to VType.jType(...) then it returns:
- java primitive type for Basic VTypes
- XxxG and XxxC appropriately for Compound VTypes



