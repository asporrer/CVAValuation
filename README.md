## Implementation of a CVA Valuation Incorporating Wrong Way Risk.

This implementation is built upon [finmath-lib](https://github.com/finmath/finmath-lib) many thanks to [cfries](https://github.com/cfries). 

### What is this project about?

This project implements a CVA valuation dropping the assumption of independence between default and payoff. Thus right way and wrong way risk can be introduced. Two approaches are implemented. First the intensity based approach and second a constrained worst case approach. 

### What is the folder structure?
The source code is located in src. The excel sheets located in ObbaExcelSheets access a .jar version of this source code and other necessary libraries located in the lib folder. They further use the Ice.jar and the Obba.jar located in the MA folder. 

All code relevant for the implementation is located in the src.main.finmath.antonsporrer.masterthesis package. The class test.net.finmath.antonsporrer.masterthesis.montecarlo.cva.IntBasedCWCCVAforDifCor is used in the IntensityBasedCVAAndCWCCVAvsCorrelation.xlsx excel sheet. Apart from that the test.net.finmath.antonsporrer.masterthesis package just contains tests, unit tests, explorative analysis and general experiments. 

### How to to make the excel sheets work?
1. Install Excel
2. Install [Obba](http://obba.info/documentation/installation/) for Excel
3. Clone this repository 
4. Open the desired sheet

After the previous steps you can run the the excel files located in ObbaExcelSheets. The excel files contain a reference to the lib folder. The lib folder contains the necessary libraries including cva-excel.jar. cva-excel.jar contains the code located in the src folder.

### How to use the excel sheets?
The color convention for the excel sheets is as follows. Green indicates a parameter which can be changed. Blue indicates an output.
The excel sheets located in the ObbaExcelSheets folder allow for a CVA valuation of a bond and a swap. The sheets CVACouponBond, CVASwap output plots of simulation paths of the short rate, default intensity, net present value of the underlying and some additional quantities. Further they provide the intensity based CVA using an approach correlating the stochastic drivers and Lando's approach and they provide the constrained worst case CVA as well as the actual net present value of the product.

The IntensityBasedCVAAndCWCCVAvsCorrelation excel sheet provides an overview over the intensity based CVA and the constrained worst case CVA for both the bond and the swap. That is to say that the different quantities are compared and especially the effect of the correlation on the intensity based CVA is examined. To this end a plot of correlation vs. CVA value is provided.

### Further remarks:
Not all functionality implemented in the Java code is used in the excel sheets for instance deterministic discounting. The sheets provide a first overview over the most important functionalities of the CVA implementation. The sheets can serve as a boilerplate to create sheets which employ further functionalities of this Java CVA implementation. 

### Disclaimer:
This code is not thoroughly tested and therefore the correctness of the results cannot be guaranteed.





