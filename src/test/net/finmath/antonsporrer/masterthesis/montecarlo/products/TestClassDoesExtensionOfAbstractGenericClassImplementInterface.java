package test.net.finmath.antonsporrer.masterthesis.montecarlo.products;

import main.net.finmath.antonsporrer.masterthesis.montecarlo.ZCBond_ProductConditionalFairValue_ModelInterface;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.product.CouponBondConditionalFairValueProcess;
import main.net.finmath.antonsporrer.masterthesis.montecarlo.product.ProductConditionalFairValueProcessInterface;

public class TestClassDoesExtensionOfAbstractGenericClassImplementInterface {

	public static void main(String[] args) {
		
		
		CouponBondConditionalFairValueProcess testClass = new CouponBondConditionalFairValueProcess(null, new double[] {1.0}, new double[] {1.0}, new double[] {1.0});
		
		ProductConditionalFairValueProcessInterface<ZCBond_ProductConditionalFairValue_ModelInterface> testClass2 = new CouponBondConditionalFairValueProcess(null, new double[] {1.0}, new double[] {1.0}, new double[] {1.0});
		
		// testClass.
		
		System.out.println( testClass instanceof ProductConditionalFairValueProcessInterface   );
		
	}

}
