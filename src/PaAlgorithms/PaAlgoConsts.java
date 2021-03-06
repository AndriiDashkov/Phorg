
package PaAlgorithms;

/**
 * Fixed sin and cos values, for some algorithms speed optimization
 * @author Andrey Dashkov
 *
 */
public class PaAlgoConsts {

	public PaAlgoConsts() {
	
	}
	
	static public double cosAr[] = {

		1.0,// k = 1 z = 0
		-1.0,// k = 2 z = 1
		6.123233995736766E-17,// k = 4 z = 2
		0.7071067811865476,// k = 8 z = 3
		0.9238795325112867,// k = 16 z = 4
		0.9807852804032304,// k = 32 z = 5
		0.9951847266721969,// k = 64 z = 6
		0.9987954562051724,// k = 128 z = 7
		0.9996988186962042,// k = 256 z = 8
		0.9999247018391445,// k = 512 z = 9
		0.9999811752826011,// k = 1024 z = 10
		0.9999952938095762,// k = 2048 z = 11
		0.9999988234517019,// k = 4096 z = 12
		0.9999997058628822,// k = 8192 z = 13
		0.9999999264657179,// k = 16384 z = 14
		0.9999999816164293,// k = 32768 z = 15
		0.9999999954041073// k = 65536 z = 16
			
			
	};
	
	static public double sinAr[] = {
			0.0,// k = 1 z = 0sin
			0.0,// k = 2 z = 1sin
			1.0,// k = 4 z = 2sin
			0.7071067811865475,// k = 8 z = 3sin
			0.3826834323650898,// k = 16 z = 4sin
			0.19509032201612825,// k = 32 z = 5sin
			0.0980171403295606,// k = 64 z = 6sin
			0.049067674327418015,// k = 128 z = 7sin
			0.024541228522912288,// k = 256 z = 8sin
			0.012271538285719925,// k = 512 z = 9sin
			0.006135884649154475,// k = 1024 z = 10sin
			0.003067956762965976,// k = 2048 z = 11sin
			0.0015339801862847655,// k = 4096 z = 12sin
			7.669903187427045E-4,// k = 8192 z = 13sin
			3.8349518757139556E-4,// k = 16384 z = 14sin
			1.917475973107033E-4,// k = 32768 z = 15sin
			9.587379909597734E-5,// k = 65536 z = 16sin
	
	};
	

}
