class UberContainer {
    open class FunctionContainerC {
        open fun funModifierC(p: AuxFaceA): Number = 0
        protected open fun funModifierF() {}
    }
    interface FunctionContainerD {
        fun <caret>funModifierD(p: CharSequence): CharSequence
        fun funBodyReturnF(p: String) = p.length
    }
    class FunctionContainerE : FunctionContainerC(), FunctionContainerD {
        override fun funModifierC(p: AuxFaceA): Int = 0
        override fun funModifierD(p: CharSequence): String = ""
    }
}
//INFO: <div class='definition'><pre><span style="color:#000080;font-weight:bold;">public</span> <span style="color:#000080;font-weight:bold;">abstract</span> <span style="color:#000080;font-weight:bold;">fun</span> <span style="color:#000000;">funModifierD</span>(
//INFO:     <span style="color:#000000;">p</span><span style="">: </span><span style="color:#000000;"><a href="psi_element://kotlin.CharSequence">CharSequence</a></span>
//INFO: )<span style="">: </span><span style="color:#000000;"><a href="psi_element://kotlin.CharSequence">CharSequence</a></span></pre></div><div class='bottom'><icon src="KotlinBaseResourcesIcons.ClassKotlin"/>&nbsp;<a href="psi_element://UberContainer.FunctionContainerD"><code><span style="color:#000000;">UberContainer.FunctionContainerD</span></code></a><br/></div>
