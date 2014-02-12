package natlab.backends.vrirGen;

import natlab.tame.callgraph.StaticFunction;

public abstract class WrapperGenerator {
	 StaticFunction func;

	public WrapperGenerator(StaticFunction func) {
		this.func = func;
	}

	public abstract String genWrapper();
}
