# OOFEM Project SoSe 2019
It’s a small FEM program to solve truss systems.

## Usage
### Creation of the structure
First you need to create a structure.
You can do so either by choosing a .csv file with the structure data using command
```
structure import
```

or providing the path to a .csv file with the structure data
```
structure importbypath
```

or to create a new empty structure
```
structure new
```

### Adding structure components
You can add Node to the structure using command
```
structure add node [x1] [x2] [x3]
```
*with* **x1, x2, x3** *coordinates of node.*

You can add an Element using command
```
structure add element [E] [A] [n1] [n2]
```
*with* **E** *the Young modulus,* **A** *the cross section area of the element and* **n1** *and* **n2** *the indices of the nodes.*

You can assign a Force to a node
```
structure add force [node id] [r1] [r2] [r3]
```
*with* **node id** *the index of the node and* **r1, r2, r3** *the components of the force.*

You can assign a Constraint to a node
```
structure add constraint [node id] [u1] [u2] [u3]
```
*with* **node id** *the index of the node and* **u1, u2, u3** *the components of the constraint provided as boolean values for the expression "if free".*

### Modifying structure components
You can modify Node of the structure using command
```
structure modify node [node id] [x1] [x2] [x3]
```
*with* **node id** *the index of the node and* **x1, x2, x3** *coordinates of node.*

You can modify an Element using command
```
structure modify element [element id] [E] [A] [n1] [n2]
```
*with* **element id** *the index of the element,* **E** *the Young modulus,* **A** *the cross section area of the element and* **n1** *and* **n2** *the indices of the nodes.*

You can assign a Force to a node
```
structure add force [node id] [r1] [r2] [r3]
```
*with* **node id** *the index of the node and* **r1, r2, r3** *the components of the force.*

You can assign a Constraint to a node
```
structure add constraint [node id] [u1] [u2] [u3]
```
*with* **node id** *the index of the node and* **u1, u2, u3** *the components of the constraint provided as boolean values for the expression "if free".*

### Further structure commands
Printing out the structure data to the console
```
structure print
```

Open viewer with the structure
```
structure draw
```

Get linear solution of the structure
```
structure solve_linear
```

Get nonlinear solution of the structure
```
structure solve_nonlinear [step number]
```
*by default* **step number** *is set to 1, but you can enter any specific amount of loading steps. The result is shown for the last one.*

### Solution commands
Printing out the solution
```
solution draw
```

Saving solution data to a .txt file
```
solution export
```

Open viewer with solved structure
```
solution draw
```

### General commands
To clear the console
```
clear
```
