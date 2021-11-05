# MyCart

A small e-commerce CLI based application written in Kotlin

## Features

- Clean UI
- User Mode
  - View products/categories
  - View recent order history
  - Purchase products
- Admin Mode
  - Can add products/categories
  - Can view user's bills/cart items
- Two databases (in memory & persistent through JSON)
- Separation of concerns (User & Admin methods)
- Tests

## Build & Run

_Use Intellij to browse the code._

- Create binaries.

```
./gradlew installDist
```

- Run the executable created for the host machine.

```
cd build/install/MyCart/bin
./MyCart
```

## ER Diagram

No relational databases are used for storing data.

## License

- [The GNU General Public License v3.0](https://www.gnu.org/licenses/gpl-3.0.txt)

```
Copyright 2021 Kaustubh Patange

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
```
