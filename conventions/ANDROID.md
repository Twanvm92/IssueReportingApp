# Android naming conventions

## Layout names

```xml
activityName_abbreviatedType_nameOfObject
```

Example: activityMain_tv_firstName (for a TextView in the activity_main layout meant for first names)

Rules:
- lowerCamelCase
- underscores between sections

Most common types:
- TextView: tv
- ImageView: iv
- EditText: et
- ListView: lv
- Button: btn


## Java variables

```java
private TextView firstName;
```

Rules:
- lowerCamelCase

## Java classes

```java
public class MyClass {
	//Class content
}
```

Rules:
- UpperCamelCase
