import java.util.ArrayList;

import chronos.models.TimeCode;
import chronos.models.Project;


public interface Employee {
    public int getId();
    public String getName();
    public double getHourlyRate();
    public ArrayList<TimeCode> getWorkedCodes();
    public ArrayList<Project> getAssignedProjects();
    public void assignProject(Project);
}
