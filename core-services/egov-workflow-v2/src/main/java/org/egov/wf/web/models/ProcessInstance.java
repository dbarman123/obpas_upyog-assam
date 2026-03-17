package org.egov.wf.web.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.egov.common.contract.request.User;
import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * A Object holds the basic data for a Trade License
 */
@ApiModel(description = "A Object holds the basic data for a Trade License")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2018-12-04T11:26:25.532+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(of = {"id"})
@ToString
public class ProcessInstance   {

        @Size(max=64)
        @JsonProperty("id")
        private String id = null;

        @NotNull
        @Size(max=128)
        @JsonProperty("tenantId")
        private String tenantId = null;

        @NotNull
        @Size(max=128)
        @JsonProperty("businessService")
        private String businessService = null;

        @NotNull
        @Size(max=128)
        @JsonProperty("businessId")
        private String businessId = null;

        @NotNull
        @Size(max=128)
        @JsonProperty("action")
        private String action = null;

        @NotNull
        @Size(max=64)
        @JsonProperty("moduleName")
        private String moduleName = null;

        @JsonProperty("state")
        private State state = null;

        @Size(max=1024)
        @JsonProperty("comment")
        private String comment = null;

        @JsonProperty("documents")
        @Valid
        private List<Document> documents = null;

        @JsonProperty("assigner")
        private User assigner = null;

        @JsonProperty("assignes")
        private List<User> assignes = null;

        @JsonProperty("nextActions")
        @Valid
        private List<Action> nextActions = null;

        @JsonProperty("stateSla")
        private Long stateSla = 0L;

        @JsonProperty("businesssServiceSla")
        private Long businesssServiceSla = null;

        @JsonProperty("previousStatus")
        @Size(max=128)
        private String previousStatus = null;

        @JsonProperty("entity")
        private Object entity = null;

        @JsonProperty("auditDetails")
        private AuditDetails auditDetails = null;

        @JsonProperty("rating")
        private Integer rating = null;

        @JsonProperty("escalated")
        private Boolean escalated = false;
        
        @JsonProperty("notes")
        private String notes = null;
        
        @JsonProperty("assignerName")
        private String assignerName = null;
        
        @JsonProperty("assignerDesignation")
        private String assignerDesignation = null;
        
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MMM dd, yyyy hh:mm:ss a", timezone = "IST")
        @JsonProperty("assignedDate")
        private Date assignedDate = null;
        
        @JsonProperty("additionalDetails")
        private Object additionalDetails = null;
        
        @JsonProperty("applicationSubmissionDatetime")
        private Long applicationSubmissionDatetime = null;
        
        @JsonProperty("latestApplicationSubmissionDatetime")
        private Long latestApplicationSubmissionDatetime = null;
        
        @JsonProperty("allowedAssignees")
        private List<User> allowedAssignees = null;


        public ProcessInstance addDocumentsItem(Document documentsItem) {
            if (this.documents == null) {
            this.documents = new ArrayList<>();
            }
            if(!this.documents.contains(documentsItem))
                this.documents.add(documentsItem);

        return this;
        }

        public ProcessInstance addNextActionsItem(Action nextActionsItem) {
            if (this.nextActions == null) {
            this.nextActions = new ArrayList<>();
            }
            this.nextActions.add(nextActionsItem);
            return this;
        }

        public ProcessInstance addUsersItem(User usersItem) {
                if (this.assignes == null) {
                        this.assignes = new ArrayList<>();
                }
                if(!this.assignes.contains(usersItem))
                        this.assignes.add(usersItem);

                return this;
        }
        public List<User> getAssignes() {
			return Optional.ofNullable(assignes).orElseGet(ArrayList::new).stream().filter(Objects::nonNull)
					.collect(Collectors.toList());
		}

}

