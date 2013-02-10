/*
 * Copyright (c) 2013 Simon Templer
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Simon Templer - initial version
 */

package eu.esdihumboldt.hale.server.api.controller;

import javax.servlet.http.HttpServletResponse

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.multipart.MultipartFile

import de.cs3d.util.logging.ALogger
import de.cs3d.util.logging.ALoggerFactory
import eu.esdihumboldt.hale.server.projects.ProjectScavenger
import eu.esdihumboldt.hale.server.projects.ScavengerException
import eu.esdihumboldt.util.io.IOUtils

/**
 * Project management controller.
 * 
 * @author Simon Templer
 */
@Controller
class Projects {

	private static final ALogger log = ALoggerFactory.getLogger(Projects)

	private final ProjectScavenger projects

	/**
	 * Create a projects controller.
	 * 
	 * @param projects the project service
	 */
	@Autowired
	Projects(ProjectScavenger projects) {
		super()
		this.projects = projects
	}

	/**
	 * Upload a new project.<br>
	 * <br>
	 * Example API call with curl:<br>
	 * <code>curl -i -F "archive=@test.zip" http://localhost:8080/api/project/test</code>
	 * 
	 * @param id the project identifier
	 * @param archive the project archive
	 * @param response the request response
	 */
	@RequestMapping(value = "/project/{id}", method = RequestMethod.POST, consumes = "multipart/form-data")
	void createProject(@PathVariable String id, @RequestPart MultipartFile archive,
			HttpServletResponse response) {
		try {
			if (id && archive) {
				File dir = projects.reserveProjectId(id)

				// try extracting the archive
				IOUtils.extract(dir, new BufferedInputStream(archive.inputStream))

				// trigger scan after upload
				projects.triggerScan()
				log.info("Successfully uploaded new project $id")

				response.status = HttpServletResponse.SC_CREATED
			}
			else {
				// bad request
				response.sendError HttpServletResponse.SC_BAD_REQUEST
			}
		} catch (ScavengerException e) {
			// could not create project
			try {
				response.sendError HttpServletResponse.SC_CONFLICT
			} catch (IOException e1) {
				// ignore
			}
		} catch (Exception e) {
			projects.releaseProjectId(id)
			log.error("Error while uploading project file", e)
			try {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage())
			} catch (IOException e1) {
				// ignore
			}
		}
	}

	/**
	 * Delete a project.<br>
	 * <br>
	 * Example API call with curl:<br>
	 * <code>curl -i -X DELETE http://localhost:8080/api/project/test</code>
	 * 
	 * @param id the project identifier
	 * @param response the request response
	 */
	//	@RequestMapping(value = "/project/{id}", method = RequestMethod.DELETE)
	void deleteProject(@PathVariable String id, HttpServletResponse response) {
		/*
		 * FIXME no method for deleting a project yet available in project
		 * scavenger
		 */

		response.status = HttpServletResponse.SC_OK
	}

	/**
	 * List the available projects.<br>
	 * <br>
	 * Example API call with curl:<br>
	 * <code>curl -i -G http://localhost:8080/api/projects</code>
	 * 
	 * @return the projects map to be converted to JSON
	 */
	@RequestMapping(value = "/projects", method = RequestMethod.GET, produces = "application/json")
	ModelMap listProjects() {
		def projectList = []

		projects.projects.each {
			projectList << [id: it, status: projects.getStatus(it)]
		}

		new ModelMap('projects', projectList)
	}

}
