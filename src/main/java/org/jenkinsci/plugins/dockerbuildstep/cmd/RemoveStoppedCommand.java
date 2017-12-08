package org.jenkinsci.plugins.dockerbuildstep.cmd;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.exception.DockerException;
import com.github.dockerjava.api.model.Container;
import hudson.Extension;
import hudson.model.AbstractBuild;
import org.jenkinsci.plugins.dockerbuildstep.log.ConsoleLogger;
import org.kohsuke.stapler.DataBoundConstructor;

import java.util.List;

/**
 * This command removes all containers that are exited or dead.
 * Mainly based on RemoveAllCommand.
 * 
 * @author philthiel
 * 
 */
public class RemoveStoppedCommand extends DockerCommand {

    private final boolean removeVolumes;

    @DataBoundConstructor
    public RemoveStoppedCommand(boolean removeVolumes) {
        this.removeVolumes = removeVolumes;
    }

    public boolean isRemoveVolumes() {
        return removeVolumes;
    }

	@Override
    public void execute(@SuppressWarnings("rawtypes") AbstractBuild build, ConsoleLogger console)
            throws DockerException {

        DockerClient client = getClient(build, null);

        List<Container> containers = client.listContainersCmd().withShowAll(true).exec();

        for (Container container : containers) {
            if (container.getStatus().startsWith("Exited") || container.getStatus().startsWith("Dead")) {
                client.removeContainerCmd((container.getId())).withRemoveVolumes(removeVolumes).exec();
                console.logInfo("Removed container with ID " + container.getId());
            }
        }
    }

    @Extension
    public static class RemoveStoppedCommandDescriptor extends DockerCommandDescriptor {
        @Override
        public String getDisplayName() {
            return "Remove all stopped containers";
        }
    }

}
