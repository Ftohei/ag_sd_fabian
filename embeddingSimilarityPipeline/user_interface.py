import embedding_util

def train_new_corpus(corpus, embedding_path, dimensionality):
    corpus = embedding_util.Corpus(corpus, embedding_path, dim = dimensionality)
    return corpus

def most_similar(corpus, document):
    # top_n == -1 für alle Ähnlichkeiten
    return corpus.fast_similarity(document, add_new_doc=True, top_n = 10)