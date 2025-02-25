package electrodynamics.common.block.subtype;

import electrodynamics.api.ISubtype;

public enum SubtypePipe implements ISubtype {
	copper(5000),
	steel(10000);

	public final long maxTransfer;

	SubtypePipe(long maxTransfer) {
		this.maxTransfer = maxTransfer;
	}

	@Override
	public String tag() {
		return "pipe" + name();
	}

	@Override
	public String forgeTag() {
		return tag();
	}

	@Override
	public boolean isItem() {
		return false;
	}
}
