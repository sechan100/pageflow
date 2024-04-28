package org.pageflow.boundedcontext.email.core;


import lombok.Value;

import java.util.HashMap;
import java.util.Map;

@Value
public class SendMailCmd {
    String to;
    String from;
    String fromName;
    String subject;
    String templatePath;
    Map<String, String> variables;

    public static BuildStep0 builder() {
        return new SendMailCmd.Builder();
    }



    // Build Steps
    public interface BuildStep0 {
        BuildStep1 to(String to);

    }
    public interface BuildStep1 {
        BuildStep2 from(String fromAddress);
        BuildStep2 from(String fromAddress, String fromName);
    }

    public interface BuildStep2 {
        BuildStep3 subject(String subject);

    }
    public interface BuildStep3 {
        BuildStep4 templatePath(String templatePath);

    }
    public interface BuildStep4 {
        BuildStep4 addVar(String key, String value);
        BuildStep4 variables(Map<String, String> variables);
        SendMailCmd build();
    }

    public static class Builder implements BuildStep0, BuildStep1, BuildStep2, BuildStep3, BuildStep4 {
        private String to;
        private String from;
        private String fromName;
        private String subject;
        private String templatePath;
        private Map<String, String> variables = new HashMap<>();

        @Override
        public BuildStep1 to(String to) {
            this.to = to;
            return this;
        }

        @Override
        public BuildStep2 from(String fromAddress) {
            return from(fromAddress, null);
        }

        @Override
        public BuildStep2 from(String fromAddress, String fromName) {
            this.from = fromAddress;
            this.fromName = fromName;
            return this;
        }

        @Override
        public BuildStep3 subject(String subject) {
            this.subject = subject;
            return this;
        }

        @Override
        public BuildStep4 templatePath(String templatePath) {
            this.templatePath = templatePath;
            return this;
        }

        @Override
        public BuildStep4 addVar(String key, String value) {
            if(this.variables == null){
                this.variables = new HashMap<>();
            }
            this.variables.put(key, value);
            return this;
        }

        @Override
        public BuildStep4 variables(Map<String, String> variables) {
            this.variables = Map.copyOf(variables);
            return this;
        }

        @Override
        public SendMailCmd build() {
            return new SendMailCmd(to, from, fromName, subject, templatePath, variables);
        }
    }



}