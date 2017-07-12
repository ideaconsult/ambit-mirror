package ambit2.rules.conditions;

import java.util.ArrayList;
import java.util.List;

public class DescriptorSolverSet implements IDescriptorSolver
{
	protected List<IDescriptorSolver> solvers = new ArrayList<IDescriptorSolver>();
	protected List<String> allDescriptorList = null;
	
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
		if (allDescriptorList == null)
			createAllDescriptorList();
		return allDescriptorList;
	}

	@Override
	public boolean isDescriptorSupported(String descrName) {
		for (IDescriptorSolver solver : solvers) 
		{	
			if (solver.isDescriptorSupported(descrName))
				return true;
		}	
		return false;
	}

	@Override
	public Object calculateDescriptor(String descrName, Object target) {
		for (IDescriptorSolver solver : solvers) 
		{	
			if (solver.isDescriptorSupported(descrName))
				return solver.calculateDescriptor(descrName, target);
		}
		return null;
	}
	
	void createAllDescriptorList()
	{
		List<String> allDescriptorList = new ArrayList<String>();
		for (IDescriptorSolver solver : solvers) 
			allDescriptorList.addAll(solver.getDescriptorList());
	}
}
