package ambit2.rules.conditions;

import java.util.ArrayList;
import java.util.List;

public class DescriptorSolverSet implements IDescriptorSolver
{
	protected List<IDescriptorSolver> solvers = new ArrayList<IDescriptorSolver>();
	
	public DescriptorSolverSet () {
	}
	
	public DescriptorSolverSet (List<IDescriptorSolver> solvers) {
		this.solvers = solvers;
	}
	
	public void addSolver(IDescriptorSolver solver) {
		solvers.add(solver);
	}
	
	public List<IDescriptorSolver> getSolvers() {
		return solvers;
	}

	public void setSolvers(List<IDescriptorSolver> solvers) {
		this.solvers = solvers;
	}

	@Override
	public List<String> getDescriptorList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isDescriptorSupported(String descrName) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object calculateDescriptor(String descrName, Object target) {
		// TODO Auto-generated method stub
		return null;
	}

}
