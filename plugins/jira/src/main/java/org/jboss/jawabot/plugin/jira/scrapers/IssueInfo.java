
package org.jboss.jawabot.plugin.jira.scrapers;

/**
 * Issue info bean; any property may return null.
 * @author Ondrej Zizka
 */
public class IssueInfo {

   public String id;
   public String title;
   public String priority;
   public String assignedTo;
   public String reportedBy;
   public String severity;
   public String status;
   public String resolution;
   
   public String url;

   // JIRA: affects versions, fix versions, compoments, security level
   // Bugzilla: TBD



   // -- Const -- //

   public IssueInfo(String id, String title) {
      this.id = id;
      this.title = title;
   }

   public IssueInfo(String id, String title, String priority, String assignedTo, String status) {
      this.id = id;
      this.title = title;
      this.priority = priority;
      this.assignedTo = assignedTo;
      this.status = status;
   }



   /**
    * @returns  [#JIRA-123] Some jira title [CLOSED, major, Ondrej Zizka]
    */
   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder(120);
      sb.append("[#").append(id).append("] ").append(title);
      sb.append(" [").append(status).append(", ").append(priority).append(", ").append(assignedTo).append("]");
      return sb.toString();
   }






   // -- get / set -- //
   
   public String getAssignedTo() {      return assignedTo;   }
   public void setAssignedTo(String assignedTo) {      this.assignedTo = assignedTo;   }
   public String getId() {      return id;   }
   public void setId(String id) {      this.id = id;   }
   public String getTitle() {      return title;   }
   public void setTitle(String title) {      this.title = title;   }
   public String getPriority() {      return priority;   }
   public void setPriority(String priority) {      this.priority = priority;   }
   public String getReportedBy() {      return reportedBy;   }
   public void setReportedBy(String reportedBy) {      this.reportedBy = reportedBy;   }
   public String getResolution() {      return resolution;   }
   public void setResolution(String resolution) {      this.resolution = resolution;   }
   public String getSeverity() {      return severity;   }
   public void setSeverity(String severity) {      this.severity = severity;   }
   public String getStatus() {      return status;   }
   public void setStatus(String status) {      this.status = status;   }
   public String getUrl() {      return url;   }
   public void setUrl(String url) {      this.url = url;   }


}// class
