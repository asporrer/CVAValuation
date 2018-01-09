## Implementation of a CVA valuation based on [finmath-lib](https://github.com/finmath/finmath-lib). 

This project implements a CVA valuation dropping the assumption of independence between default and payoff. Thus right way and wrong way risk can be introduced.
Two approaches are implemented. First the intensity based approach and second a constrained worst case approach.

The excel sheets located in the ObbaExcelSheets folder allow for a CVA valuation of a bond and a swap. Not all functionality implemented in the Java code is used in the excel sheets for instance deterministic discounting. The sheets provide a first overview over the most important functionalities of the CVA implementation. The sheets can serve as a boilerplate to create sheets which use further functionalities of the Java CVA implementation.

Steps to use the excel sheets:
1. Install Excel
2. Install [Obba](http://obba.info/documentation/installation/) for Excel
3. Clone this repository 

After the previous steps you can run the the excel files located in ObbaExcelSheets.




