/*
 *
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.cateyes.core.metadata;
import java.util.ArrayList;
import java.util.Collection;
import javax.xml.bind.*;
import javax.xml.bind.annotation.*;
/**
 * @author sankooc
 */

@SuppressWarnings("restriction")
@XmlRootElement
public class Metadata {
	@XmlRootElement
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class Program {
		@XmlElement
		private String vid;
		@XmlElement
		private String type;

		public Program(String vid, String type) {
			super();
			this.vid = vid;
			this.type = type;
		}

		public Program() {
		}
	}
	
	
	@XmlElementWrapper(name="list")
	@XmlElement(name="program")
	private Collection<Program> programs;



	public void setPrograms(Collection<Program> programs) {
		this.programs = programs;
	}



	public static void main(String[] args) throws JAXBException{
		Metadata meta = new Metadata();
		Collection<Program> ps = new ArrayList<Program>();
		ps.add(new Program("akt","lalal"));
		ps.add(new Program("akt3","lalal4"));
		meta.programs = ps;
		JAXBContext context = JAXBContext.newInstance(new Class[]{Metadata.class,Program.class});
	    Marshaller m = context.createMarshaller();
	    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
	    m.marshal(meta, System.out);

	}
}
