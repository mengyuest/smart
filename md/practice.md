# Database discussion
18 July, Meng Yue

> ### **DynaMIT** runs simulation based on two sources of inputs. One describes the time-dependent counts for the traffic flow, coming from surveillance sensor data, whereas the other one describes the static attributes of the network and the behavior model. The former one could be defined as **_data_** and the latter one **_parameters_**.

>### **_Parameters_** rely on lots of other things like weather conditions and lightings, hence there is no set of default parameters can reproduce satisfied traffic dynamics in all the circumstances. Therefore, the need of the database comes up.

> ### Besides, **_data_** served as an "input" for the new cycle of calculation is actually an "output" from the last cycle, coming from DynaMIT or MITSIM which depends on the type of simulation (open-loop or closed-loop). This historical records helps to evaluate and predict the state of the network, hence also need to be stored in database for high-efficient searching.

> ### The following session first presents the details for these two kinds of database, and later raises some questions which needs to be discussed.

## Database for parameters
The database for parameters is set up before. Each time choose the best pattern of each segment (this require an algorithm for patter match procedure) to fit the demanding and then use the correlated parameters to update (or initiate if is the first cycle of simulation) the link parameters.

### Table Format
| Segment Id | Pattern Id | Param 1 | Param 2 | Param 3 | Param 4 | ... |
|:----------:|:----------:|:-------:|:-------:|:-------:|:-------:|:---:|
|     1      |       0        |  5   |  7      | 7     |  9       | ... |
|     1      |       1        |  2   |   5     | 9     |   8      | ... |
|   1        |      2         |  6   |    3    | 10    |   5      | ... |
|     2      |       0        |  8   |  7      | 7     |  10      | ... |
|     2      |       1        |  3   |   5     | 9     |   8      | ... |
|   2        |      2         |  3   |    3    | 10    |   5      | ... |
|     ...    |      ...       |  ... |   ...   | ...   |...       | ... |

## Database for output data

### Introduction
The output data mainly includes count, speed, density and queue length of each segment. These data is obtained by the output files and then save into database.

### Table Format
| Segment Id | Time  Interval | Count | Density | Speed | Queue length |
|:----------:|:--------------:|:-----:|:-------:|:-----:|:------------:|
|     1      |       0        |  10   |  7      | 7     |  10          |
|     1      |       1        |  12   |   5     | 9     |   8          |
|   1        |      2         |  13   |    3    | 10    |   5          |
|     2      |       0        |  10   |  7      | 7     |  10          |
|     2      |       1        |  12   |   5     | 9     |   8          |
|   2        |      2         |  13   |    3    | 10    |   5          |
|     ...    |      ...       |  ...  |   ...   | ...   |...           |



## Questions
* What is the current situation for the database work for DynaMIT?
* The database for the parameters. Don't know how to do.
* Is all the system running on the same computer, which means no need to set up Internet connection between the server and the client?
* Who I can seek for programming tech help? Is there any programmer who can help me?
* Is there any performance requirement?
