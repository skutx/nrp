package Helper;

import Attributes.Employee;
import Attributes.SchedulingPeriod;
import Attributes.Skill;

import java.util.ArrayList;
import java.util.List;

public class InitialSolution {
    private Helper helper;

    public InitialSolution(SchedulingPeriod schedulingPeriod) {
        helper = new Helper(schedulingPeriod, null);
    }

    /**
     * Entwirft eine Initiallösung, bei dem nur harte Restriktionen berücksichtigt werden.
     *
     * @return solutionMatrix   Liste von zweidimensionalen int-Arrays (pro Tag einen Eintrag in der Liste)
     * int-Array: Tabelle der Schichten pro Mitarbeiter (1, arbeitet in Schicht x, 0 nicht)
     */
    public List<int[][]> createSolution() {
        List<int[][]> solutionMatrix = new ArrayList<>();
        List<Employee> employeeList = helper.getEmployeeList();
        int employeeSize = employeeList.size();
        int employeeCount = 0;

        //für jeden Tag
        for (int i = 0; i < helper.getDaysInPeriod(); i++) {
            List<RequirementsForDay> requirementsForDay = helper.getRequirementsForDay(i);
            int shiftTypeSize = requirementsForDay.size();
            int[][] day = new int[shiftTypeSize][employeeSize];

            //für jede Schicht
            for (int j = 0; j < shiftTypeSize; j++) {
                //für jeden Mitarbeiter
                for (int k = employeeCount; k < employeeSize; k++) {
                    Employee employee = employeeList.get(k);
                    //wenn es eine DH Schicht ist prüfe ob der aktuelle Employee auch HeadNurse ist
                    if (requirementsForDay.get(j).getShiftID().contains("DH")) {
                        if (employee.getSkills().contains(Skill.HEAD_NURSE) && requirementsForDay.get(j).getDemand() > 0) {

                            //bevor Employee für Schicht zugeordnet wird, prüfe ob sie schon an dem Tag arbeitet
                            boolean worksToday = false;
                            for (int x = 0; x < j; x++) {
                                if (day[x][k] == 1) {
                                    worksToday = true;
                                }
                            }
                            //wenn sie noch in keiner Schicht zugeteilt wurde, teile zu und setze Bedarf -1
                            if (!worksToday) {
                                day[j][k] = 1;
                                int demand = requirementsForDay.get(j).getDemand() - 1;
                                requirementsForDay.get(j).setDemand(demand);
                            }
                        }
                    } else {
                        if (requirementsForDay.get(j).getDemand() > 0) {
                            //bevor Employee für Schicht zugeordnet wird, prüfe ob sie schon an dem Tag arbeitet
                            boolean worksToday = false;
                            for (int x = 0; x < j; x++) {
                                if (day[x][k] == 1) {
                                    worksToday = true;
                                }
                            }
                            //wenn sie noch in keiner Schicht zugeteilt wurde, teile zu und setze Bedarf -1
                            if (!worksToday) {
                                day[j][k] = 1;
                                int demand = requirementsForDay.get(j).getDemand() - 1;
                                requirementsForDay.get(j).setDemand(demand);
                            }
                        }
                    }

                    if (requirementsForDay.get(j).getDemand() == 0) {
                        //Merke die Stelle von Employee und breche ab
                        if (k >= employeeSize - 1) {
                            employeeCount = 0;
                        } else {
                            employeeCount = k + 1;
                        }
                        break;
                    }
                    //wenn Employee beim letzten angekommen ist, fange for-Schleife von vorne an
                    if (k == employeeSize - 1) {
                        k = -1;
                    }
                }
            }
            solutionMatrix.add(day);
        }
        return solutionMatrix;
    }
}
