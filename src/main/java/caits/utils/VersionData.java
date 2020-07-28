package caits.utils;

public class VersionData {
	final public int major;
	final public int minor;
	final public int release;
	final public int build;
	public String description;
	public String place;
	
	public VersionData(int major, int minor, int release, int build) {
		super();
		this.major = major;
		this.minor = minor;
		this.release = release;
		this.build = build;
		this.description = null;
		this.place = null;
	}
	
	@Override
	public String toString() {
		return String.format("%d.%d.%d.%d", major, minor, release, build);
	}
	
	public boolean equal(int major, int minor, int release, int build) {
		return this.major == major && this.minor == minor && this.release == release && this.build == build;
	}
	
	public boolean equal(VersionData version) {
		return version!=null && equal(version.major, version.minor, version.release, version.build);
	}
	
	public boolean less(int major, int minor, int release, int build) {
		if (this.major != major) return this.major > major;
		else if (this.minor != minor) return this.minor > minor;
		else if (this.release != release) return this.release > release;
		else return this.release > build;
	}	
	
	public boolean less(VersionData version) {
		return version==null || less(version.major, version.minor, version.release, version.build);
	}	
	
	public VersionData incBuild() {
		return new VersionData(major, minor, release, build+1);
	}

	public VersionData incRelease() {
		return new VersionData(major, minor, release+1, build+1);
	}
	
	public VersionData incMinor() {
		return new VersionData(major, minor+1, 0, build+1);
	}	

	public VersionData incMajor() {
		return new VersionData(major+1, 0, 0, build+1);
	}	
	
}
