// =========================================================================== //
//                                                                             //
// Copyright 2008-2011 Andrew Casey, Jun Li, Jesse Doherty,                    //
//   Maxime Chevalier-Boisvert, Toheed Aslam, Anton Dubrau, Nurudeen Lameed,   //
//   Amina Aslam, Rahul Garg, Soroush Radpour, Olivier Savary Belanger,        //
//   Laurie Hendren, Clark Verbrugge and McGill University.                    //
//                                                                             //
//   Licensed under the Apache License, Version 2.0 (the "License");           //
//   you may not use this file except in compliance with the License.          //
//   You may obtain a copy of the License at                                   //
//                                                                             //
//       http://www.apache.org/licenses/LICENSE-2.0                            //
//                                                                             //
//   Unless required by applicable law or agreed to in writing, software       //
//   distributed under the License is distributed on an "AS IS" BASIS,         //
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  //
//   See the License for the specific language governing permissions and       //
//   limitations under the License.                                            //
//                                                                             //
// =========================================================================== //

package natlab;

import natlab.backends.vrirGen.VRIRGenerator;
import natlab.options.VRIROptions;

/**
 * Main entry point for McLab compiler. Includes a main method that deals with
 * command line options and performs the desired functions.
 */
public class Main {

	/**
	 * Main method deals with command line options and execution of desired
	 * functions.
	 */
	public static void main(String[] args) throws Exception {
		if (args.length == 0) {
			System.err.println("No options given\nTry -help for usage");
			return;
		}
		VRIROptions options = new VRIROptions();
		options.parse(args);
		run(options);

	}

	public static void run(VRIROptions options) throws Exception {

		// VRIR options
		if (options.vrir() || options.mex()) {
			if (options.files().isEmpty()) {
				if (!options.main().isEmpty()) {
					/*
					 * If the user provided an entry point function and did not
					 * provide a separate file, Use the main file as the input
					 * file.
					 */
					options.files().add(options.main());
				} else {
					System.err
							.println("No files provided, must have at least one file.");
				}
				
			}
			if (options.main() == null || options.main().length() == 0) {
				options.setMain(options.files().get(0));
			}
			VRIRGenerator.compile(options);
		} else {
			McLabCore.run(options);
		}
	}
}
