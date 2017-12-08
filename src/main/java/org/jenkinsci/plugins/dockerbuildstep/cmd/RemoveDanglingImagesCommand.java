package org.jenkinsci.plugins.dockerbuildstep.cmd;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.exception.DockerException;
import com.github.dockerjava.api.model.Image;
import hudson.Extension;
import hudson.model.AbstractBuild;
import org.jenkinsci.plugins.dockerbuildstep.log.ConsoleLogger;
import org.kohsuke.stapler.DataBoundConstructor;

import java.util.List;

/**
 * This command removes all dangling images
 *  Mainly based on RemoveAllCommand
 * 
 * @author philthiel
 * 
 */
public class RemoveDanglingImagesCommand extends DockerCommand {

	@DataBoundConstructor
	public RemoveDanglingImagesCommand() {
	}

	@Override
	public void execute(@SuppressWarnings("rawtypes") AbstractBuild build, ConsoleLogger console)
			throws DockerException {

		DockerClient client = getClient(build, null);

		List<Image> images = client.listImagesCmd().withDanglingFilter(true).exec();

		for (Image image : images) {
				client.removeImageCmd(image.getId()).exec();
				console.logInfo("Removed image with ID " + image.getId());
		}
	}
	
	@Extension
	public static class RemoveDanglingImagesCommandDescriptor extends DockerCommandDescriptor {
		@Override
		public String getDisplayName() { return "Remove dangling images"; }
	}

}
