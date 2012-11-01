/*
 * Copyright (c) 2012 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.hale.server.webapp.components;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.util.time.Duration;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import org.wicketstuff.html5.markup.html.Progress;

import de.fhg.igd.osgi.util.OsgiUtils;
import eu.esdihumboldt.hale.server.progress.ProgressService;

/**
 * Panel showing job status.
 * 
 * @author Simon Templer
 */
public class JobPanel extends Panel {

	private static final long serialVersionUID = 1567693599697840713L;

	/**
	 * Create a job panel.
	 * 
	 * @param id the component ID
	 * @param jobFamily the job family, may be <code>null</code> if all jobs
	 *            should be shown
	 * 
	 * @see IJobManager#find(Object)
	 */
	public JobPanel(String id, final Serializable jobFamily) {
		super(id);

		setOutputMarkupId(true);

		// update panel
		add(new AjaxSelfUpdatingTimerBehavior(Duration.milliseconds(1500)));

		// job list
		final IModel<? extends List<Job>> jobModel = new LoadableDetachableModel<List<Job>>() {

			private static final long serialVersionUID = 7277175702043541004L;

			@Override
			protected List<Job> load() {
				return Arrays.asList(Job.getJobManager().find(jobFamily));
			}

		};

		final ListView<Job> jobList = new ListView<Job>("jobs", jobModel) {

			private static final long serialVersionUID = -6740090246572869212L;

			/**
			 * @see ListView#populateItem(ListItem)
			 */
			@Override
			protected void populateItem(final ListItem<Job> item) {
				final boolean odd = item.getIndex() % 2 == 1;
				if (odd) {
					item.add(AttributeModifier.replace("class", "odd"));
				}

				// status
//				String status;
//				switch (job.getState()) {
//				case Job.WAITING:
//					status = "waiting";
//					break;
//				case Job.SLEEPING:
//					status = "sleeping";
//					break;
//				case Job.RUNNING:
//					status = "running";
//					break;
//				case Job.NONE:
//				default:
//					status = "unknown";
//				}
//				item.add(new Label("status", status));

				// name
				item.add(new Label("name", item.getModelObject().getName()));

				final IModel<eu.esdihumboldt.hale.server.progress.Progress> progressModel = new LoadableDetachableModel<eu.esdihumboldt.hale.server.progress.Progress>() {

					private static final long serialVersionUID = 2666038645533292585L;

					@Override
					protected eu.esdihumboldt.hale.server.progress.Progress load() {
						ProgressService ps = OsgiUtils.getService(ProgressService.class);
						if (ps == null) {
							return null;
						}
						return ps.getJobProgress(item.getModelObject());
					}

				};

				// progress
				Progress progress = new JobProgress("progress", progressModel);
				item.add(progress);

				// task name
				item.add(new Label("task", progressModel.getObject().getTaskName()));

				// subtask
				item.add(new Label("subtask", progressModel.getObject().getSubTask()));
			}

		};
		add(jobList);

		add(new WebMarkupContainer("nojobs") {

			private static final long serialVersionUID = -7752350858497246457L;

			@Override
			public boolean isVisible() {
				return jobModel.getObject().isEmpty();
			}

		});
	}

}
