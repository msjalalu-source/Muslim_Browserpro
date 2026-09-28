package com.muslim.browser.pro

import com.muslim.browser.pro.browser.BengaliTranslator
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class TilcayoPerformanceTest {

    val tilcayoSegments = listOf(
        "The tilcayo (Leopardus tilcayo;   til-KYE-oh; Spanish pronunciation: [tilˈkaʝo] ) is a species of small wild cat in the genus Leopardus native to Bolivia. Identified through a genetic study of tiger cats published in September 2026, the Tilcayo is the first extant cat species formally described since 1923. The tilcayo was already known to local communities under that name. Its appearance is similar to that of other tiger cat species such as the oncilla, and its status as a genetically distinct population was not documented by zoologists until the 2026 study.",
        "Discovery",
        "The tilcayo is the first extant felid species formally identified since the identification of the Pampas cat (L. fasciatus) in 1923. The tilcayo was already known to local communities but had not been scientifically identified as genetically distinct from other tiger cat species.",
        "Biologist and National Geographic Society explorer Paola Nogales-Ascarrunz first encountered the species in 2017, when the Senda Verde Wildlife Sanctuary in Bolivia contacted her about a cat that had been brought in. For around a year prior, the cat had been living with a man in La Paz who found it as a kitten and initially believed it to be a domestic cat before realizing it required the care of a wildlife sanctuary. During that time, he had attempted to feed it \"noodles, rice, and eggs\". Nogales-Ascarrunz was intrigued by the cat and photographed it, believing at the time that it was a unique-looking member of the related species Leopardus tigrinus (Oncilla).",
        "In 2019, while preparing a booklet of Bolivian cat species, Nogales-Ascarrunz realized that the individual from the wildlife sanctuary appeared distinct from L. tigrinus. In 2026, the tilcayo was identified as a distinct species using genomic analysis.",
        "Etymology",
        "The name \"tilcayo\" was already used for the species by local communities in the region. Publications on Bolivian fauna predating 2026 associate the term tilcayo with Leopardus tigrinus. It was originally reported that the exact etymology of the term is unknown, as it did not appear to be of Spanish, Quechuan, or Aymara origin. The term has been passed down in the local communities through generations, with its meaning reportedly lost in transfer. However, in an interview following the announcement of the species identification, Nogales-Ascarrunz stated that native Aymara speakers had reached out through social media suggesting that the term likely comes from t'ili kayu (lit. 'tiny foot').",
        "Distribution and habitat",
        "The tilcayo was found near the town of Arapata in Nor Yungas province, in the Bolivian department of La Paz, at an elevation of 1,570 m (5,150 ft). This locality is positioned in the mountainous Yungas forest region, a cloud forest, on the eastern slope of the Andes mountain range.  The Yungas ecosystem is under pressure. Bolivia cloud-forest cat is currently listed as two vulnerable species by IUCN.",
        "Taxonomy",
        "A genetic analysis of 38 cats from South and Central America, including 26 individuals in the Leopardus tigrinus species complex, revealed that the tigrinus complex consists of five distinct species, including L. tilcayo, which is believed to have split off from other Leopardus species more than 1.4 million years ago. The study incorporated genetic material from previously unsampled tiger cats in the Guianas region, near where L. tigrinus was originally described in 1775. Jonas Lescroart, an evolutionary ecologist at the University of Antwerp and co-lead author of the study, called it \"the most complete genetic study yet\" of tiger cats.",
        "Phylogenomic results obtained with the high-depth consensus nuclear sequence dataset are displayed below:",
        "Characteristics",
        "The fur of the tilcayo is light brown with irregularly shaped rosettes. Its rosettes are large and dark brown to black in color, and are either open or partially closed –they do not coalesce. It also has short, round ears, long whiskers, and a scrunched-up face. Like many species in the Leopardus genus, it is smaller than a domestic cat. It has a head–body length of 42.5–50 cm (16.7–19.7 in), with a tail length of 25–26.5 cm (9.8–10.4 in). It weighs 1.5–2.0 kg (3.3–4.4 lb). The tilcayo so closely resemble other tiger cat species they are considered a cryptic species, difficult to distinguish from others by morphology alone.",
        "Conservation",
        "As of September 2026, L. tilcayo has not been formally assessed by the IUCN Red List. It currently recognizes two tiger cat species, L. tigrinus and L. guttulus, both listed as vulnerable. According to study co-lead author Jonas Lescroart, the research team shared their findings with the IUCN ahead of a planned reassessment of tiger cats. Study co-author Tadeu de Oliveira, an ecologist at the State University of Maranhão in Brazil, said the tiger cat species identified in the study are found in habitats that are \"vanishing fast\".",
        "The individual which first came to the attention of Nogales-Ascarrunz remains in protective captivity at the Senda Verde animal refuge in Bolivia. Some ten years old, he has been nicknamed \"Tigrino\".",
        "The habitat loss, deforestation and mining have been identified as threats to the species: UMSA researchers have identified hunting resulting from conflicts with local inhabitants when the cats prey on domestic poultry, as well as the keeping of the animals as pets, as potential threats to the Tilcayo.",
        "The species has not yet been formally assessed by the International Union for Conservation of Nature (IUCN). Researchers have indicated that further studies of its distribution, population, reproduction and habitat requirements are needed before its conservation status can be established.",
        "Research",
        "Some of the initial genetic research on Leopardus tilcayo was conducted at the Molecular Genetics Laboratory of the Higher University of San Andrés (UMSA) in Bolivia. According to the university, the samples examined included tissue, hair and teeth, from which DNA was extracted and subjected to initial genetic analyses. These results were subsequently compared with samples obtained by researchers from other countries as part of the international study that led to the formal description of the species.",
        "Researcher Patricia Mollinedo stated that this initial stage involved at least two years of work, including DNA extraction and processing of the samples and other stages of the analysis. The infrastructure and equipment used belonged to UMSA, while laboratory supplies were financed through external resources obtained for the research project.",
        "Bolivian researchers Paola Nogales-Ascarrunz and Enzo Aliaga-Rossel participated in the international research that established the genetic identity of the species. UMSA subsequently reported that both researchers were recognized by Department of La Paz for their participation in the discovery.",
        "See also",
        "Sunda leopard cat (Prionailurus javanensis) – small wild cat recognized as a distinct species in 2017",
        "Chinese mountain cat (Felis bieti) – another small wild cat recognized as a distinct species since 2017"
    )

    @Test
    fun runTilcayoPerformanceBenchmark() = runBlocking {
        val totalChars = tilcayoSegments.sumOf { it.length }
        val totalWords = tilcayoSegments.sumOf { it.split(Regex("\\s+")).filter { w -> w.isNotBlank() }.size }
        val uniqueSegments = tilcayoSegments.distinct()

        println("=== PREPARING BENCHMARK ===")
        println("Input segments: ${tilcayoSegments.size}")
        println("Input words: $totalWords")
        println("Input characters: $totalChars")
        println("Unique text segments: ${uniqueSegments.size}")

        // First Run: Clean cache
        BengaliTranslator.clearCache()
        BengaliTranslator.TranslationStats.reset()

        // Full batch run 1 (Uncached)
        println("=== EXECUTING FIRST RUN (FULL PIPELINE) ===")
        val startFirstRun = System.currentTimeMillis()
        val firstRunResult = BengaliTranslator.translateBatch(tilcayoSegments)
        val endFirstRun = System.currentTimeMillis()
        val firstRunDurationMs = endFirstRun - startFirstRun
        val firstRunSeconds = firstRunDurationMs / 1000.0

        val translatedMap = if (firstRunResult.isSuccess) firstRunResult.getOrThrow() else emptyList()
        val totalTranslatedNodes = translatedMap.filterIndexed { idx, trans -> trans != tilcayoSegments[idx] }.size

        val totalReqs = BengaliTranslator.TranslationStats.totalRequests
        val successfulReqs = BengaliTranslator.TranslationStats.successfulRequests
        val failedReqs = BengaliTranslator.TranslationStats.failedRequests
        val http429s = BengaliTranslator.TranslationStats.http429Count
        val retries = BengaliTranslator.TranslationStats.retryCount
        val durations = BengaliTranslator.TranslationStats.requestDurations

        val minTime = if (durations.isNotEmpty()) durations.minOrNull() ?: 0L else 0L
        val maxTime = if (durations.isNotEmpty()) durations.maxOrNull() ?: 0L else 0L
        val avgTime = if (durations.isNotEmpty()) durations.average().toLong() else 0L

        // Full batch run 2 (Cached)
        println("=== EXECUTING SECOND RUN (CACHE VERIFICATION) ===")
        val startSecondRun = System.currentTimeMillis()
        val secondRunResult = BengaliTranslator.translateBatch(tilcayoSegments)
        val endSecondRun = System.currentTimeMillis()
        val secondRunDurationMs = endSecondRun - startSecondRun
        val secondRunSeconds = secondRunDurationMs / 1000.0

        val cacheImprovement = if (firstRunDurationMs > 0) {
            ((firstRunDurationMs - secondRunDurationMs).toDouble() / firstRunDurationMs) * 100.0
        } else 0.0

        println("\n=== LARGE WIKIPEDIA TRANSLATION TEST ===")
        println("Input words: $totalWords")
        println("Input characters: $totalChars")
        println("Unique text segments: ${uniqueSegments.size}")
        println("Concurrency: 3")
        println("Total API requests: $totalReqs")
        println("Successful requests: $totalTranslatedNodes/${uniqueSegments.size}")
        println("Failed requests: ${uniqueSegments.size - totalTranslatedNodes}/${uniqueSegments.size}")
        println("HTTP 429: $http429s")
        println("Retries: $retries")
        println("Minimum request time: $minTime ms")
        println("Average request time: $avgTime ms")
        println("Maximum request time: $maxTime ms")
        println("TOTAL TRANSLATION TIME:")
        println("$firstRunDurationMs ms")
        println(String.format("%.2f seconds", firstRunSeconds))
        println("FIRST RUN: " + String.format("%.2f seconds", firstRunSeconds))
        println("SECOND RUN: " + String.format("%.4f seconds", secondRunSeconds))
        println(String.format("Cache improvement: %.2f %%", cacheImprovement))
        println("========================================\n")
    }
}
